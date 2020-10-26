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
 * @brief Generates one ISO14443-A anti-collision process -- by-hand --
 */

/*
 * nfc-anticol.sh
 *
 * A demo of the Chameleon Mini Live Debugger mini terminal scripting
 * language to show key features and syntax that it can accomplish in this
 * application. The idea is that this platform makes it quick and simple to
 * automate running reusable batches of Chameleon terminal commands
 * on the go with the Droid device.
 *
 * https://github.com/maxieds/ChameleonMiniLiveDebugger
 *
 * Author: Maxie D. Schmidt (@maxieds)
 * Created: 2020.10.26
 */

$_SAK_FLAG_ATS_SUPPORTED=0x20
$_CASCADE_BIT=0x04

// Note that the array initializer list syntax only
// works with byte type literals:
$abtReqa = { 0x26 }
$abtSelectAll = { 0x93, 0x20 }
$abtSelectTag = { 0x93, 0x70, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 }
$abtRats = { 0xe0, 0x50, 0x00, 0x00 }
$abtHalt = { 0x50, 0x00, 0x00, 0x00 }

// Always start with Cascade Level 1 (CL1)
$szCL = 1
$szAts = 0

// Send the 7 bits request command specified in ISO 14443A (0x26)
$abtRx = $$(Sprintf("SEND %s", AsHexString($abtReqa)))
if($abtRx->isError) {
     Printf("Error: No tag available\n");
     Exit(-1);
}
$abtAtqa = GetSubarray($abtRx, 0, 2)

// Anti-collision
$abtRx = $$(Sprintf("SEND %s", AsHexString($abtSelectAll)))

// Check answer
if (($abtRx[0] ^ $abtRx[1] ^ $abtRx[2] ^ $abtRx[3] ^ $abtRx[4]) != 0) {
     Printf("WARNING: BCC check failed!\n");
}

// Save the UID CL1
// memcpy(abtRawUid, abtRx, 4);
$abtRawUid = GetSubarray($abtRx, 0, 4)

// Prepare and send CL1 Select-Command
$abtSelectTag[2:] = $abtRx[0:5]
$abtSelectTag = AppendCRC16($abtSelectTag, 7)
$abtRx = $$(Sprintf("SEND %s", AsHexString($abtSelectTag)))
$abtSak = $abtRx[0]

// Test if we are dealing with a CL2
if($abtSak & $_CASCADE_BIT) {
     $szCL = 2
     // Check answer
     if($abtRawUid[0] != 0x88) {
          Printf("WARNING: Cascade bit set but CT != 0x88!\n")
     }
}

// We have to do the anti-collision for cascade level 2:
if(szCL == 2) {

    // Prepare CL2 commands
    $abtSelectAll[0] = 0x95;
    // Anti-collision
    $abtRx = $$(Sprintf("SEND %s", AsHexString($abtSelectAll)))
    // Check answer
    if(($abtRx[0] ^ $abtRx[1] ^ $abtRx[2] ^ $abtRx[3] ^ $abtRx[4]) != 0) {
         Printf("WARNING: BCC check failed!\n");
    }
    // Save UID CL2
    $abtRawUid[4:4] = $abtRx[0:4]
    // Selection
    $abtSelectTag[0] = 0x95;
    $abtSelectTag[2:7] = $abtRx[0:5]
    $abtSelectTag = AppendCRC16($abtSelectTag, 9)
    $abtRx = $$(Sprintf("SEND %s", AsHexString($abtSelectTag)))
    $abtSak = $abtRx[0];
    // Test if we are dealing with a CL3
    if($abtSak & $CASCADE_BIT) {
         $szCL = 3;
         // Check answer
         if($abtRawUid[0] != 0x88) {
              Printf("WARNING: Cascade bit set but CT != 0x88!\n");
         }
    }

    // We have to do the anti-collision for cascade level 3
    if($szCL == 3) {

         // Prepare and send CL3 AC-Command
         $abtSelectAll[0] = 0x97;
         $abtRx = $$(Sprintf("SEND %s", AsHexString($abtSelectAll)))
         // Check answer
         if(($abtRx[0] ^ $abtRx[1] ^ $abtRx[2] ^ $abtRx[3] ^ $abtRx[4]) != 0) {
              Printf("WARNING: BCC check failed!\n");
         }
         // Save UID CL3
         $abtRawUid[8:12] = $abtRx[0:4]
         // Prepare and send final Select-Command
         $abtSelectTag[0] = 0x97;
         $abtSelectTag[2:7] = $abtRx[0:5]
         $abtSelectTag = AppendCRC16($abtSelectTag, 7)
         $abtRx = $$(Sprintf("SEND %s", AsHexString($abtSelectTag)))
         $abtSak = $abtRx[0];
    }

}

// Request ATS (this only applies to tags that support ISO 14443A-4)
if(($abtRx[0] & $SAK_FLAG_ATS_SUPPORTED)) {
    $abtRats = AppendCRC16($abtRats, 2)
    $abtRx = $$(Sprintf("SEND %s", AsHexString($abtRats)))
    if(not $abtRx->isError) {
         $abtAts = $abtRx[0:$szRx]
         $szAts = szRx;
    }
}

// Done, halt the tag now
$abtHalt = AppendCRC16($abtHalt, 2)
$$(Sprintf("SEND %s", AsHexString($abtHalt)))

Printf("| ********************************************************** |\n\n")
Printf("Found tag with\n UID: ");
if($szCL == 1) {
   Printf("%02x%02x%02x%02x", $abtRawUid[0], $abtRawUid[1], $abtRawUid[2], $abtRawUid[3])
}
else if($szCL == 2) {
   Printf("%02x%02x%02x",     $abtRawUid[1], $abtRawUid[2], $abtRawUid[3])
   Printf("%02x%02x%02x%02x", $abtRawUid[4], $abtRawUid[5], $abtRawUid[6], $abtRawUid[7])
}
else if($szCL == 3) {
   Printf("%02x%02x%02x",      $abtRawUid[1], $abtRawUid[2], $abtRawUid[3]);
   Printf("%02x%02x%02x",      $abtRawUid[5], $abtRawUid[6], $abtRawUid[7]);
   Printf("%02x%02x%02x%02x",  $abtRawUid[8], $abtRawUid[9], $abtRawUid[10], $abtRawUid[11]);
}
Printf("\n");
Printf("ATQA: %02x%02x\n SAK: %02x\n", $abtAtqa[1], $abtAtqa[0], $abtSak);
if($szAts > 1) {
     Printf("ATS: %s", AsHexString($abtAts, $szAts))
}
Printf("\n| ********************************************************** |\n")

Exit(0)
