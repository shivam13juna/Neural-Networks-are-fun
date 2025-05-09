const { PDFDocument, rgb, StandardFonts } = require('pdf-lib');
const fs = require('fs').promises;
const path = require('path');
const fontkit = require('fontkit');
const pdfjsLib = require('pdfjs-dist/build/pdf.js'); // Standard path for older versions
pdfjsLib.GlobalWorkerOptions.workerSrc = require.resolve('pdfjs-dist/build/pdf.worker.js'); // Standard path for older versions

async function extractTextWithPdfJs(pdfPath) {
    const data = new Uint8Array(await fs.readFile(pdfPath));
    const pdfDoc = await pdfjsLib.getDocument({ data }).promise;
    let fullText = '';
    for (let i = 1; i <= pdfDoc.numPages; i++) {
        const page = await pdfDoc.getPage(i);
        const textContent = await page.getTextContent();
        // textContent.items is an array of text segments. Join them.
        fullText += textContent.items.map(item => item.str).join(' \n') + '\n'; // Add space and newline between items, and newline after page
    }
    return fullText;
}

async function extractTextAndRebuildPdf() {
    try {
        const outputPdfPath = 'murlis_reformatted_noto.pdf';
        const finalPdfDoc = await PDFDocument.create();
        finalPdfDoc.registerFontkit(fontkit);

        const fontFileName = 'Noto_Sans_Devanagari/static/NotoSansDevanagari-Regular.ttf';
        const fontPath = path.resolve(__dirname, '..', fontFileName);
        let customFont;
        try {
            const fontBytes = await fs.readFile(fontPath);
            customFont = await finalPdfDoc.embedFont(fontBytes, { subset: false }); // Subsetting still disabled
            console.log(`Custom Hindi font '${fontPath}' loaded and embedded (subsetting disabled).`);
        } catch (fontError) {
            console.error(`FATAL ERROR: Could not load Hindi font: ${fontError.message}`);
            return;
        }

        const targetFontSize = 12;
        const pageMargin = 50;
        const lineHeight = targetFontSize * 1.4;

        let currentPage = finalPdfDoc.addPage();
        let { width, height } = currentPage.getSize();
        let currentY = height - pageMargin;

        const pdfFilesToProcess = ['m8.pdf', 'm9.pdf'];

        for (const pdfFile of pdfFilesToProcess) {
            const currentPdfPath = path.resolve(__dirname, '..', pdfFile);
            console.log(`\nProcessing ${pdfFile} with pdf.js...`);
            try {
                const extractedText = await extractTextWithPdfJs(currentPdfPath);

                if (!extractedText || extractedText.trim() === '') {
                    console.warn(`No text extracted from ${pdfFile} using pdf.js. Skipping.`);
                    continue;
                }
                console.log(`Extracted ${extractedText.length} characters from ${pdfFile} using pdf.js.`);
                const sampleText = extractedText.substring(0, 300); // Log first 300 chars
                console.log(`---- Sample of extracted text from ${pdfFile} (pdf.js): ----`);
                console.log(sampleText);
                console.log(`-----------------------------------------------------------`);

                const lines = extractedText.split('\n');

                for (const line of lines) {
                    const trimmedLine = line.trim();
                    if (trimmedLine === '') continue;

                    // Basic text wrapping attempt (measure text width)
                    let textWidth = customFont.widthOfTextAtSize(trimmedLine, targetFontSize);
                    const availableWidth = width - 2 * pageMargin;

                    if (textWidth > availableWidth) {
                        // Simple split, might break words. More sophisticated wrapping is complex.
                        // For now, we'll just draw it and let it overflow if it's too long for one line after a simple split attempt
                        // Or, one could implement a more complex word-wrapping algorithm here.
                        // This example will just proceed, potentially overflowing.
                        console.warn(`Warning: Line from ${pdfFile} might be too long for the page width: "${trimmedLine.substring(0,50)}..."`);
                    }

                    if (currentY < pageMargin + lineHeight) {
                        currentPage = finalPdfDoc.addPage();
                        ({ width, height } = currentPage.getSize());
                        currentY = height - pageMargin;
                        console.log('Added a new page.');
                    }

                    currentPage.drawText(trimmedLine, {
                        x: pageMargin,
                        y: currentY,
                        font: customFont,
                        size: targetFontSize,
                        color: rgb(0, 0, 0),
                        lineHeight: lineHeight,
                    });
                    currentY -= lineHeight;
                }
                console.log(`Finished writing text from ${pdfFile} to the new PDF.`);

            } catch (err) {
                console.error(`Error processing ${pdfFile} with pdf.js: ${err.message}.`);
                console.error(err.stack); // Log stack trace for more details
            }
        }

        const finalPdfBytes = await finalPdfDoc.save();
        const finalOutputPath = path.resolve(__dirname, '..', outputPdfPath);
        await fs.writeFile(finalOutputPath, finalPdfBytes);
        console.log(`\n--------------------------------------------------------------------`);
        console.log(`Successfully created '${outputPdfPath}' in your project root directory.`);
        console.log(`Text extracted using pdf.js. Subsetting is disabled.`);
        console.log(`--------------------------------------------------------------------`);

    } catch (error) {
        console.error("Error in extractTextAndRebuildPdf function:", error);
    }
}

// Call the main function
extractTextAndRebuildPdf();
