/* mifare-classic-tool.sh:
 * Script to print a summary of a Mifare Classic tags.
 * Some of the supported scripting code shown below is
 * intended to demonstrate syntax for modifying a more
 * complicated operation - like reading full tag data.
 * See: nfc-anticol.sh and
 *      https://github.com/maxieds/MifareClassicToolLibrary
 *      for hints on identifying tag components like
 *      tag sector count, block count, manufacturer ID,
 *      total size in bytes, etc.
 * Note: The examples below use the Chameleon `SEND_RAW`
 *       command with vendor-specific APDU commands to
 *       attempt to perform common operations. YMMV.
 */

/* Compare the keys compiled here:
 * https://github.com/maxieds/ChameleonMiniLiveDebugger/tree/
 *                            master/app/src/main/SampleScripts/SampleData.
 */
$sampleKeysList = {{
     "ffffffffffff",
     "a0a1a2a3a4a5",
     "d3f7d3f7d3f7",
     "a0b0c0d0e0f0",
     "a1b1c1d1e1f1",
     "a0a1a2a3a4a5",
     "b0b1b2b3b4b5",
     "4d3a99c351dd",
     "1a982c7e459a",
     "000000000000",
     "d3f7d3f7d3f7",
     "aabbccddeeff"
}}

/* Task 1: Fetch and report the Mifare Classic tag UID: */
$uidResp = $$("SEND_RAW FF CA 00 00 00")
if ($uidResp->isError) {
     Print(">>> Tag did not respond with a UID string.\n")
} else {
     $uidStr = Replace($uidResp->data, "..(?!$)", "$0:")
     Printf(">>> Tag UID: %s\n", $uidStr)
}

/* Task 2: Load Authentication Keys (into reader key slot 00|01): */
$mfKeyStr = Substr($knownKeysList[0], 0, 6)
$chamLoadStr = Sprintf("SEND_RAW FF 82 00 00 06 %s", Replace($mfKeyStr, "..(?!$)", "$0 "))
$chamLoadResult = $$($chamLoadStr)

/* Task 3: Authenticate Sector (with key from slot 60(A)|61(B)):
 *         BL is the block number within the sector.
 *         Send: FF 86 00 00 05 01 00 BL 60 00
 */

/* Task 4: Read Block:
 *         BL is the block number (e.g., 04 for the first data block of sector 1),
 *         Lc is the length of data to read (e.g., 10 hex for 16 bytes).
 *         Send: FF B0 00 BL Lc
 */

/* Task 5: Write Block:
 *         Send: FF D6 00 BL Lc D1 D2 ... DN
 */

/* NDEF formats: Reading / Writing / Creating NFC-type
 *               digital business cards.
 * See: https://learn.adafruit.com/adafruit-pn532-rfid-nfc/ndef
 * See: https://github.com/nfc-tools/libfreefare/blob/
 *                         master/examples/mifare-desfire-create-ndef.c#L214
 */