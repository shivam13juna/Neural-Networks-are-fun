# Proposed File Consolidations and Merges

This document outlines a plan to reduce file count by merging, relocating, or removing redundant files/configurations. No changes are applied yet—this is purely a proposal.

## 1. Gitignore Consolidation
- Merge `.idea/.gitignore` into the root `.gitignore` and then remove the IDE-specific `.gitignore`.
- Ensure all build artifacts, IDE settings, and temporary files are covered by the root `.gitignore`.

## 2. IDE Configuration Cleanup
- Remove both `.idea/` and `.vscode/` folders from version control entirely.
- If needed, add a minimal `.editorconfig` at root for consistent formatting.

## 3. Documentation Merge
- Create a unified `README.md` at project root by combining contents of `prd.md` (rename and integrate).
- Delete `prd.md` after migration.

## 4. Gradle Script Simplification
- Consolidate module-level `app/build.gradle.kts` into the root `build.gradle.kts` as a multi-module configuration.
- Inline `settings.gradle.kts` logic into the unified root build script and remove the standalone settings file.

## 5. Version Catalog & Properties
- Inline dependency versions from `libs.versions.toml` directly into the root build script or `gradle.properties`.
- Delete `libs.versions.toml` once versions are migrated.

## 6. Proguard Configuration
- Move `app/proguard-rules.pro` to a central `config/proguard.pro` at root.
- Delete the old file under `app/`.

## 7. Remove Redundant Binaries
- Delete `app/bin/` directory (contains generated class files).
- Ensure `.gitignore` excludes any remaining output folders (`.gradle/`, `build/`, etc.).

## 8. Keystore Handling
- Move `keystore/release.jks` out of source control into a secure vault and add it to `.gitignore`.

## 9. Gradle Wrapper
- Keep `gradle/wrapper/gradle-wrapper.properties`, but remove `gradle-wrapper.jar` if desired and use the project’s Gradle wrapper distribution instead.

---
**Next Steps:**
1. Review and approve the above plan.
2. Execute merges and deletions in PRs module-by-module.
3. Validate build and IDE configurations after each merge.
