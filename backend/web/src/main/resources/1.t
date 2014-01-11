var indexes = {
1: 3,
2: 5,
3: 6,
4: 8
}
value =123;
var newV = '';
for (var i=0; i< 10; i++) {
    var obj = value[i];
   var index = indexes[i];
    if (igs.utils.isNotEmpty(obj)) {
        newV[index] = obj;
    } else {
        newV[index] = ' ';
    }
}