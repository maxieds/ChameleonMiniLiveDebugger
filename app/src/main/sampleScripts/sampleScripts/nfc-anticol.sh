/*-
 * Free/Libre Near Field Communication (NFC) library
 *
 * Libnfc historical contributors:
 * Copyright (C) 2009      Roel Verdult
 * Copyright (C) 2009-2013 Romuald Conty
 * Copyright (C) 2010-2012 Romain Tartière
 * Copyright (C) 2010-2013 Philippe Teuwen
 * Copyright (C) 2012-2013 Ludovic Rousseau
 * See AUTHORS file for a more comprehensive list of contributors.
 * Additional contributors of this file:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *  1) Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  2 )Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Note that this license only applies on the examples, NFC library itself is under LGPL
 *
 */

/**
 * @file nfc-anticol.c
 * @brief Generates one ISO14443-A anti-collision process "by-hand"
 */

/* nfc-anticol.sh */

$SAK_FLAG_ATS_SUPPORTED=0x20
$CASCADE_BIT=0x04

// Note that the array initializer list syntax only
// works with byte literals:
$abtReqa = { 0x26 }
$abtSelectAll = { 0x93, 0x20 }
$abtSelectTag = { 0x93, 0x70, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
$abtRats = { 0xe0, 0x50, 0x00, 0x00 }
$abtHalt = { 0x50, 0x00, 0x00, 0x00 }

// memcpy(abtAtqa, abtRx, 2);
// transmit_bytes(abtSelectAll, 2);
// if ((abtRx[0] ^ abtRx[1] ^ abtRx[2] ^ abtRx[3] ^ abtRx[4]) != 0) {
//   printf("WARNING: BCC check failed!\n");
// }

// Save the UID CL1
// memcpy(abtRawUid, abtRx, 4);

// Prepare and send CL1 Select-Command
// memcpy(abtSelectTag + 2, abtRx, 5);
// iso14443a_crc_append(abtSelectTag, 7);
// transmit_bytes(abtSelectTag, 9);
// abtSak = abtRx[0];

// Test if we are dealing with a CL2
// if (abtSak & CASCADE_BIT) {
// szCL = 2; //or more
// Check answer
//   if (abtRawUid[0] != 0x88) {
//     printf("WARNING: Cascade bit set but CT != 0x88!\n");
//     }
// }

//// TODO: More later ...
/*
  if (szCL == 2) {
    // We have to do the anti-collision for cascade level 2

    // Prepare CL2 commands
    abtSelectAll[0] = 0x95;

    // Anti-collision
    transmit_bytes(abtSelectAll, 2);

    // Check answer
    if ((abtRx[0] ^ abtRx[1] ^ abtRx[2] ^ abtRx[3] ^ abtRx[4]) != 0) {
      printf("WARNING: BCC check failed!\n");
    }

    // Save UID CL2
    memcpy(abtRawUid + 4, abtRx, 4);

    // Selection
    abtSelectTag[0] = 0x95;
    memcpy(abtSelectTag + 2, abtRx, 5);
    iso14443a_crc_append(abtSelectTag, 7);
    transmit_bytes(abtSelectTag, 9);
    abtSak = abtRx[0];

    // Test if we are dealing with a CL3
    if (abtSak & CASCADE_BIT) {
      szCL = 3;
      // Check answer
      if (abtRawUid[0] != 0x88) {
        printf("WARNING: Cascade bit set but CT != 0x88!\n");
      }
    }

    if (szCL == 3) {
      // We have to do the anti-collision for cascade level 3

      // Prepare and send CL3 AC-Command
      abtSelectAll[0] = 0x97;
      transmit_bytes(abtSelectAll, 2);

      // Check answer
      if ((abtRx[0] ^ abtRx[1] ^ abtRx[2] ^ abtRx[3] ^ abtRx[4]) != 0) {
        printf("WARNING: BCC check failed!\n");
      }

      // Save UID CL3
      memcpy(abtRawUid + 8, abtRx, 4);

      // Prepare and send final Select-Command
      abtSelectTag[0] = 0x97;
      memcpy(abtSelectTag + 2, abtRx, 5);
      iso14443a_crc_append(abtSelectTag, 7);
      transmit_bytes(abtSelectTag, 9);
      abtSak = abtRx[0];
    }
  }

  // Request ATS, this only applies to tags that support ISO 14443A-4
  if (abtRx[0] & SAK_FLAG_ATS_SUPPORTED) {
    iso_ats_supported = true;
  }
  if ((abtRx[0] & SAK_FLAG_ATS_SUPPORTED) || force_rats) {
    iso14443a_crc_append(abtRats, 2);
    if (transmit_bytes(abtRats, 4)) {
      memcpy(abtAts, abtRx, szRx);
      szAts = szRx;
    }
  }

  // Done, halt the tag now
  iso14443a_crc_append(abtHalt, 2);
  transmit_bytes(abtHalt, 4);

  printf("\nFound tag with\n UID: ");
  switch (szCL) {
    case 1:
      printf("%02x%02x%02x%02x", abtRawUid[0], abtRawUid[1], abtRawUid[2], abtRawUid[3]);
      break;
    case 2:
      printf("%02x%02x%02x", abtRawUid[1], abtRawUid[2], abtRawUid[3]);
      printf("%02x%02x%02x%02x", abtRawUid[4], abtRawUid[5], abtRawUid[6], abtRawUid[7]);
      break;
    case 3:
      printf("%02x%02x%02x", abtRawUid[1], abtRawUid[2], abtRawUid[3]);
      printf("%02x%02x%02x", abtRawUid[5], abtRawUid[6], abtRawUid[7]);
      printf("%02x%02x%02x%02x", abtRawUid[8], abtRawUid[9], abtRawUid[10], abtRawUid[11]);
      break;
  }
  printf("\n");
  printf("ATQA: %02x%02x\n SAK: %02x\n", abtAtqa[1], abtAtqa[0], abtSak);
  if (szAts > 1) { // if = 1, it's not actual ATS but error code
    if (force_rats && ! iso_ats_supported) {
      printf(" RATS forced\n");
    }
    printf(" ATS: ");
    print_hex(abtAts, szAts);
  }
*/