# ITSOHUB Certificate System: Future Extensions Roadmap

This document outlines the strategic extensions planned for the ITSOHUB Certificate & Verification ecosystem. The current implementation provides a robust foundation for secure issuance, public verification, and administrative management.

## 1. QR Code Embedding
*   **Objective**: Bridge the gap between digital/physical certificates and the verification portal.
*   **Details**: 
    *   Generate a dynamic QR code for each certificate containing a unique URL: `https://verify.itsohub.com/v/{verificationCode}`.
    *   Standardize the QR placement on PDF layouts for consistent scanning experience.
    *   Enable "Scan & Verify" feature within the mobile app using the `VerificationEngine`.

## 2. PDF Certificate Generation
*   **Objective**: Provide users with formal, downloadable, and printable documents.
*   **Details**:
    *   **Architecture**: Use a headless PDF engine (e.g., Cloud Functions with PDFKit or client-side Skia/Compose Canvas export).
    *   **Security**: Embed invisible watermarks and metadata containing the `verificationCode`.
    *   **Aesthetics**: Implement multi-language institutional templates with professional typography and digital signatures.

## 3. e-Devlet Integration
*   **Objective**: Achieve national-level recognition and accessibility for Turkish citizens.
*   **Details**:
    *   Integrate with the **e-Devlet (Digital Turkey)** API ecosystem to sync certificates with the user's official profile.
    *   Enable "Barcode Query" (Barkodlu Belge Doğrulama) compatibility with government standards.
    *   Allow users to share verified ITSOHUB certificates directly through the e-Devlet "My Certificates" (Sertifikalarım) portal.

## 4. External Verification API
*   **Objective**: Facilitate 3rd party integrations for HR and B2B partners.
*   **Details**:
    *   **RESTful Endpoints**: Dedicated public endpoints for external systems to verify credentials Programmatically.
    *   **Webhook Support**: Notify external systems (e.g., a company's internal HR portal) when a user earns a specific certificate.
    *   **OAuth2 Scopes**: Allow users to grant limited read-access to their certificate portfolio for partner platforms.

## 5. Blockchain Anchoring (Optional)
*   **Objective**: Provide decentralized, immutable proof of existence and timestamping.
*   **Details**:
    *   **Anchor Type**: Hash-on-chain anchoring (e.g., Polygon or Avalanche) where only the `sha256(certificateId + verificationCode)` is stored.
    *   **Proof of Integrity**: Enable users to prove their certificate was issued at a specific time without relying solely on ITSOHUB databases.
    *   **Decentralized Identity (DID)**: Aligning certificates with W3C DID standards for long-term verifiable credentials (VCs).

---
**Status**: Current core architecture is designed to support all the above extensions without breaking changes to the Firestore schema or security model.
