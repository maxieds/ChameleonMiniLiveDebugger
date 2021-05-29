$arr = {{ $ctri, $ctri + 3, 0x04, -1 }}
$arr = {{ 0x04, -1 }}
$atr0 = 10
$str = ArrayToString($arr)
Printf("Array Values: %s\n\n", $str)
Printf("Array Values: %s\n\n", ArrayToString($arr))
