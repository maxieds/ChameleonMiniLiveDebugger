/* example-script1.sh */

$cmdOutput = $$("CONFIG=MF_DESFIRE")
Printf("CMD-RESP(%03d) [%s] ... \n", $cmdOutput->respCode, $cmdOutput->respText)

$cmdOutput = $$("CONFIG?")
Printf("CmdName=%s (%d) [%s] -> \"%s\" %s / %s\n", 
       $cmdOutput->cmdName, $cmdOutput->respCode, $cmdOutput->respText,
       $cmdOutput->data, $cmdOutput->isError ? "XX" : "OK", 
       $cmdOutput->isTimeout ? "TMT" : "NO-TMT")

myBkptLabel:

$tagUID = $$("UID?")
$tagUID = Replace("..(?!$)", "$0" + ":")
Print("Tag UID = ", $tagUID)

Exit(0)
