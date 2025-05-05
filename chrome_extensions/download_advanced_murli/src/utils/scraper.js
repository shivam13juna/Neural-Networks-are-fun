// Pure DOM-parser helpers (imported by service worker)
export async function findLatestMonthUrl() {
  const res = await fetch('https://www.shivbabas.org/blog/tags/advance-murli');
  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, 'text/html');
  // Find anchors linking to monthly Advance Murli posts
  const anchors = [...doc.querySelectorAll('a')].filter(a =>
    /\/post\/advance-murli-[a-z]+-\d{4}$/i.test(a.getAttribute('href') || '')
  );
  if (!anchors.length) throw new Error('No month posts found');
  // Extract URL and date
  const posts = anchors.map(a => {
    const article = a.closest('article');
    const timeElem = article?.querySelector('time');
    const date = timeElem
      ? new Date(timeElem.dateTime)
      : new Date(a.textContent);
    return { url: a.href, date };
  });
  // Sort descending by date and return latest
  posts.sort((a, b) => b.date - a.date);
  return posts[0].url;
}

export async function getHindiPdfLinks(monthUrl) {
  const res = await fetch(monthUrl);
  if (!res.ok) throw new Error(`Failed to fetch month page: ${res.status}`);
  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, 'text/html');
  // Find the Hindi section header
  const h2 = [...doc.querySelectorAll('h2')].find(h =>
    /Hindi Murli PDF/i.test(h.textContent)
  );
  if (!h2) throw new Error('Hindi section header not found');
  const links = [];
  let node = h2.nextElementSibling;
  while (node && node.tagName !== 'H2') {
    if (node.tagName === 'A' && /\.pdf$/i.test(node.getAttribute('href'))) {
      links.push({ href: node.href, text: node.textContent.trim() });
    }
    node = node.nextElementSibling;
  }
  return links;
}
