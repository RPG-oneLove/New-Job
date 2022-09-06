package org.example;

import java.io.*;
import java.util.Scanner;

public class Main6 {

    static boolean isNumeric(String str) {
        try{
            Long.parseLong(str);
            return true;
        } catch(NumberFormatException e) {return false;}
    }
    //next function is from stackoverflow. I will think about how it works later because I think it may be
    //united with my code.
    public static long countLineFast(String fileName) {
        long lines = 0;
        try (InputStream is = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean endsWithoutNewLine = false;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n')
                        ++count;
                }
                endsWithoutNewLine = (c[readChars - 1] != '\n');
            }
            if (endsWithoutNewLine) {
                ++count;
            }
            lines = count;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    public static void main(String[] args) throws Exception {

        File file = new File("lng.txt");
        int linesNumber = (int)countLineFast("lng.txt");
        System.out.println("Number of lines: " + linesNumber);
        long[][] numbers = new long[(linesNumber)][];
        int defectCount = 0;
        //Array of all numbers
        //This cycle reads file with numbers in array "numbers" and clears defects (throws them to the end of array)
        try (Scanner fileScanner = new Scanner(file)) {
            for (int i = 0; i < linesNumber - defectCount; i++) {
                boolean isDefect = false;
                String[] wordsDivided = fileScanner.next().split(";");
                int length = wordsDivided.length;
                numbers[i] = new long[length];
                for (int j = 0; j < length; j++) {
                    String word = wordsDivided[j].substring(0,wordsDivided[j].length()-1).replaceFirst("\"", "");
                    if (isNumeric(word)) {
                        numbers[i][j] = Long.parseLong(word);
                    } else if (word.isEmpty()) {
                        numbers[i][j] = 0L; //Maybe null would be better
                    } else { // Defect case
                        numbers[i] = null;
                        defectCount++;
                        i--;
                        break;
                    }
                }
            }
        }

        //Next code makes array "groups" with numbers of lines that must be in groups
        int numbersSize = numbers.length - defectCount;
        int[] groups = new int[numbersSize];
        int groupsNumber = 0;
        int nextGroup = 1;
        for(int iExamined = 0; iExamined < numbersSize; iExamined++) {
            boolean sameLine = false;
            boolean oldLine = false;
            int rowExamined = numbers[iExamined].length;
            for (int jExamined = 0; jExamined < rowExamined; jExamined++) {
                for (int iCheck = iExamined + 1; iCheck < numbersSize && jExamined < numbers[iCheck].length; iCheck++) {
                    if (numbers[iExamined][jExamined] == numbers[iCheck][jExamined] && numbers[iCheck][jExamined] != 0L) {
                        if (groups[iExamined] != 0 && groups[iCheck] == 0) {
                            groups[iCheck] = groups[iExamined];
                        }
                        if (groups[iCheck] != 0) {
                            if (groups[iExamined] == 0) groups[iExamined] = groups[iCheck];
                            else {
                                if (groups[iCheck] < groups[iExamined]) {
                                    groupsNumber--;
                                    for (int i = 0; i < numbersSize; i++) {
                                        if (groups[i] == groups[iExamined]) groups[iExamined] = groups[iCheck];
                                    }
                                } else if (groups[iCheck] > groups[iExamined]) {
                                    groupsNumber--;
                                    for (int i = 0; i < numbersSize; i++) {
                                        if (groups[i] == groups[iCheck]) groups[iCheck] = groups[iExamined];
                                    }
                                }
                            }
                        }
                        if (groups[iCheck] == 0 && groups[iExamined] == 0) {
                            groupsNumber++;
                            groups[iCheck] = nextGroup;
                            groups[iExamined] = nextGroup;
                            nextGroup++;
                        }
                    }
                }
            }
        }

        System.out.println();
        System.out.println("Analyzing done!");

        if (groupsNumber == 0) System.out.println("There is no one group!"); else System.out.println("Sum of groups: " + groupsNumber);
    }
}