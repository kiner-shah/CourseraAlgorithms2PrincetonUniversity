@echo off

echo %time%
java -cp C:\Users\user\Downloads\algs4.jar;. BurrowsWheeler - < couscous.txt > couscous.bwt
echo %time%

echo %time%
java -cp C:\Users\user\Downloads\algs4.jar;. BurrowsWheeler + < stars.bwt
echo %time%

echo %time%
java -cp C:\Users\user\Downloads\algs4.jar;. BurrowsWheeler - < abra.txt | java -cp C:\Users\user\Downloads\algs4.jar;. BurrowsWheeler +
echo %time%

echo %time%
java -cp C:\Users\user\Downloads\algs4.jar;. MoveToFront - < aesop.txt > aesop.mtf
echo %time%

echo %time%
java -cp C:\Users\user\Downloads\algs4.jar;. MoveToFront - < abra.txt | java -cp C:\Users\user\Downloads\algs4.jar;. MoveToFront +
echo %time%
