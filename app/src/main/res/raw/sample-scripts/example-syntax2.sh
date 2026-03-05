/* example-syntax2.sh */

Printf("Example Script V2:\n > Loops and slices\n")

$ctri = 3
while($ctri != 0) {
     Printf(" -- LOOP I = %02d\n", $ctri)
     if($ctri == 0) {
          Printf("LOOP I = %02d (!!!! LAST ITERATION === 0 !!!!)\n", $ctri)
     }
     else {
          Printf("LOOP I = %02d\n", $ctri)
     }
     $ctri = $ctri + -1
}
Printf("CTRI = %d\n", $ctri)
Printf("\n")

$arr = {{ $ctri, $ctri + 3, 0x04, -1 }}
$arr = {{ 0x04, -1 }}
$atr0 = 10
$str = ArrayToString($arr)
Printf("Array Values: %s\n\n", $str)
Printf("Array Values: %s\n\n", ArrayToString($arr))

Printf("$arr[0:] -> %s\n", ArrayToString($arr[0:]))
Printf("$arr[0:1] -> %s\n", ArrayToString($arr[0:1]))
Printf("$arr[:2] -> %s\n", ArrayToString($arr[:2]))
Printf("$arr[1:] -> %s\n", ArrayToString($arr[1:]))

$arr[1:2] = [ 0x00, 0x01, 0x02 ]
Printf("New Array Values  I: %s\n\n", ArrayToString($arr))

$arr[2:] = [ 0xff, 0xff ]
Printf("New Array Values II: %s\n\n", ArrayToString($arr))

Exit(0)
