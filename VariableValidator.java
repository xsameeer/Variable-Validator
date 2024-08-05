package cmsc256;

/************
 *Class name: Variable Validator
 *Class Description: This program will create an AVL tree of the reserved words and operators in the Java language,
 *                   and the query language keywords (if included in the input file). It also implements
 *                   ProgramParserInterface.
 *
 * (04/26/2024)
 * Updated Class Description: This Program takes in a Java File and parses through it to check for
 *                            valid and invalid identifiers in the file, also comparing it to the
 *                            AVL tree of reserved words. It also is made to handle and ignore
 *                            delimiters, comments, String literals, and legal/illegal
 *                            identifier conventions.
 --------------------------------------------------------------------------------------------
 *Name: Sameer Ali
 *Version date: 04/26/2024
 *CMSC 256 901
 *
 ***************/

import cmsc256.AVLTree.AVLNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Scanner;

public class VariableValidator implements ProgramParserInterface {

    // Private member variables
    private File javaFileName;
    private File keywordFileName;
    private AVLTree<String> keywordTree;


    // Parameterless constructor
    public VariableValidator() {
    }

    // Constructor with two file names as arguments
    public VariableValidator(String keywordFileName, String javaFileName) {
        setKeywordFile(keywordFileName);
        setJavaFile(javaFileName);
    }

    // Constructor with one file name as argument
    public VariableValidator(String keywordFileName) {
        setKeywordFile(keywordFileName);
    }

    // Getter for Java file name
    @Override
    public String getJavaFileName() {
        return javaFileName.getName();
    }

    // Setter for Java file
    @Override
    public void setJavaFile(String javaFileName) {
        if (javaFileName == null) {
            throw new IllegalArgumentException("Java file name cannot be null");
        }
        //initialize file
        File file = new File(javaFileName);
        // if file is not a regular file or not readable
        if (!file.isFile() || !file.canRead() || !file.exists()) {
            throw new IllegalArgumentException("Java file is invalid, cannot be read, or does not exist");
        }
        this.javaFileName = file;
    }

    // Getter for keyword file name
    @Override
    public String getKeywordFileName() {
        return keywordFileName.getName();
    }

    // Setter for keyword file
    @Override
    public void setKeywordFile(String keywordFileName) {
        if (keywordFileName == null) {
            throw new IllegalArgumentException("keyword file name cannot be null");
        }
        //initialize file
        File file = new File(keywordFileName);
        // if file is not a regular file or not readable
        if (!file.isFile() || !file.canRead() || !file.exists()) {
            throw new IllegalArgumentException("Java file is invalid, cannot be read, or does not exist");
        }
        this.keywordFileName = file;
    }

    // Method to create keyword tree from file
    @Override
    public AVLTree<String> createKeywordTree() throws FileNotFoundException {
        // Check if keyword file exists
        if (!keywordFileName.exists()) {
            throw new FileNotFoundException();
        }
        // Initialize keywordTree as new AVL tree
        keywordTree = new AVLTree<>();
        // Read keyword file and insert keywords into tree
        try (Scanner in = new Scanner(keywordFileName)) {
            //while file still has a next line
            while (in.hasNextLine()) {
                //Initialize a string as next line
                String line = in.nextLine().trim();
                // insert line without spaces into the AVL tree
                keywordTree.insert(line);
            }
        }
        return keywordTree;
    }

    // Method to perform inorder traversal of keyword tree
    @Override
    public String getInorderTraversal() throws FileNotFoundException {
        if (keywordTree == null) {
            createKeywordTree();
        }
        return getInorderTraversal(keywordTree.getRoot());
    }

    // Helper method for inorder traversal
    private String getInorderTraversal(AVLNode node) {
        if (node == null) return "";

        // Traverse left subtree
        String traversal = getInorderTraversal(node.getLeft());
        // Add the current node
        traversal += node.getElement() + " ";
        // Traverse right subtree
        traversal += getInorderTraversal(node.getRight());

        return traversal;
    }

    // Method to perform preorder traversal of keyword tree
    @Override
    public String getPreorderTraversal() throws FileNotFoundException {
        if (keywordTree == null) {
            createKeywordTree();
        }
        return getPreorderTraversal(keywordTree.getRoot());
    }

    // Helper method for preorder traversal
    private String getPreorderTraversal(AVLNode node) {
        if (node == null) return "";

        // Add the current node
        String traversal = node.getElement() + " ";
        // Traverse left subtree
        traversal += getPreorderTraversal(node.getLeft());
        // Traverse right subtree
        traversal += getPreorderTraversal(node.getRight());
        return traversal;
    }

    // Method to perform postorder traversal of keyword tree
    @Override
    public String getPostorderTraversal() throws FileNotFoundException {
        if (keywordTree == null) {
            createKeywordTree();
        }
        return getPostorderTraversal(keywordTree.getRoot());
    }

    // Helper method for postorder traversal
    private String getPostorderTraversal(AVLNode node) {
        if (node == null) return "";

        // Traverse left subtree
        String traversal = getPostorderTraversal(node.getLeft());
        // Traverse right subtree
        traversal += getPostorderTraversal(node.getRight());
        // Add the current node
        traversal += node.getElement() + " ";
        return traversal;
    }

    public Map<String, Integer> getValidJavaIdentifiers() throws FileNotFoundException {
        Map<String, Integer> validId = new HashMap<>();
        //create Keyword tree
        try {
            createKeywordTree();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Check if javaFile exists
        if (!javaFileName.exists()) {
            throw new FileNotFoundException();
        }
        // Scanner to read the file
        Scanner input = new Scanner(javaFileName);
        int lineNumber = 0;
        // Iterates over each line in the file
        while (input.hasNextLine()) {
            lineNumber++;
            String line = input.nextLine().trim();

            //Skips lines containing comments
            if (line.contains("/*") || line.contains("//")){
                while (input.hasNextLine()) {
                    if (line.contains("*/")) { //skips multi-line comments
                        line = input.nextLine().trim();
                        lineNumber++;
                        break;
                    } else if (line.contains("//")){ //skips single line comments
                        int beginIndex = line.indexOf("//");
                        String subLine = line.substring(beginIndex);
                        line = line.replace(subLine,"");
                        break;
                    }else{ //Skips lines inside the multi line comments
                        line = input.nextLine().trim();
                        lineNumber++;
                    }
                }
            }

            line = removeStringsAndDelimiters(line); //removes delimiters and strings from the line
            if (!line.isEmpty()) {
                String[] wordList = line.split(" "); // split the words in the line into an array
                for (String word : wordList) {
                    // check if token is a valid identifier, not a reserved word, and is not empty
                    if (isValidToken(word) && isValidIdentifier(word) && !word.isEmpty()) {
                        // Add the word to the map or update its occurrence count
                        validId.put(word, validId.getOrDefault(word, 0) + 1);
                    }
                }
            }
        }
        return validId; // return map of Valid java Identifiers
    }

    public Map<String, List<Integer>> getInvalidJavaIdentifiers() {
        Map<String, List<Integer>> invalidId = new HashMap<>();
        //create Keyword tree
        try {
            createKeywordTree();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Scanner to read the file
        int lineNumber = 0;
        Scanner input;
        try {
            input = new Scanner(javaFileName);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        // Iterate over each line in the file
        while (input.hasNextLine()) {
            lineNumber++;
            String line = input.nextLine().trim();

            //Skips lines containing comments
            if (line.contains("/*") || line.contains("//")){
                while (input.hasNextLine()) {
                    if (line.contains("*/")) { //skips multi-line comments
                        line = input.nextLine().trim();
                        lineNumber++;
                        break;
                    } else if (line.contains("//")){ //skips single line comments
                        int beginIndex = line.indexOf("//");
                        String subLine = line.substring(beginIndex);
                        line = line.replace(subLine,"");
                        break;
                    }else{ //Skips lines inside the multi line comments
                        line = input.nextLine().trim();
                        lineNumber++;
                    }
                }
            }

            line = removeStringsAndDelimiters(line); //removes strings and delimiters from the line
            if (!line.isEmpty()) {
                String[] wordList = line.split(" "); // split the words in the line into an array
                for (String word : wordList) {
                    if (!word.isEmpty()) { // checks if the token is empty
                        if (!isValidToken(word)) { //checks if token is part of the reserved words KeywordTree
                        continue;
                        // checks if token is not a valid Identifier and doesn't consist of only numbers
                        } else if (!isValidIdentifier(word) && !isNumber(word)){
                            if (!invalidId.containsKey(word)) { // Check if the word is not already in the map
                                invalidId.put(word, new ArrayList<>()); //add word to map with an empty list
                            }
                            // Else retrieve the list of line numbers for the current word
                            List<Integer> lineNumbers = invalidId.get(word);
                            //checks if current line number is not already in the list
                            if (!lineNumbers.contains(lineNumber)) {
                                lineNumbers.add(lineNumber); //add current line number to the list
                                invalidId.put(word, lineNumbers); // Update map with new list of line numbers
                            }
                        }
                    }
                }
            }
        }
        return invalidId; // return map of invalid java identifiers
    }


    public boolean isValidToken(String word) {
        // Check if the token is a reserved word in KeywordTree
        if (keywordTree.find(word) != null) {
            return false; //false if it exists in the tree
        }
        return true; // true if it does not exist in the tree
    }

    public boolean isValidIdentifier(String word) {
        // Check if the token is not empty
        if (word.isEmpty()) {
            return false;
        }
        // Check if the first character is a valid identifier start character
        char firstChar = word.charAt(0);
        if (!(Character.isLetter(firstChar) || firstChar == '_' || firstChar == '$')) {
            return false;
        }

        for (int i = 1; i < word.length(); i++) {
            char ch = word.charAt(i);
            // Checks if the characters in the token are not a valid identifier character
            if (!(Character.isLetterOrDigit(ch) || ch == '_' || ch == '$')) {
                return false;
            }
        }
        return true;
    }

    public boolean isNumber (String input){ // checks if the token is only a number
        int count = 0; // initialized for the count of letters in the token
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            // If the character is a letter
            if (Character.isLetter(ch)) {
                count++; // increment count
            }
        }
        //Verifies if the token consists of at least 1 letter
        if (count > 0) {
            return false;
        }
        return true;
    }

    private String removeStringsAndDelimiters(String input) { // Removes strings and delimiters in a line
        String placeHolder = "";
        boolean insideStringLiteral = false; // indicates if inside a string or not

        if (input.contains("\"")){ // checks if line contains a string literal
            for (int i = 0; i < input.length(); i++) { //parses through the line by each character
                char ch = input.charAt(i);

                if (insideStringLiteral) { //if inside of string
                    if (ch == '\"') { //if second quotation is reached
                        insideStringLiteral = false; // false to exit the if statement
                    }
                    continue; // Skip characters inside string
                }
                // Check for start of first quotation
                if (ch == '\"') {
                    insideStringLiteral = true;
                    placeHolder += ch;
                    continue;
                }

                placeHolder += ch; // add the characters to placeHolder
            }
            input = placeHolder; //Update input to reflect new line after String literals are removed
        }
        //return the line after removing delimiters and math operators
        return input
                // Remove delimiters
                .replace("\"", "")
                .replace("{", " ")
                .replace("}", " ")
                .replace("(", " ")
                .replace(")", " ")
                .replace("[", " ")
                .replace("]", " ")
                .replace(",", " ")
                .replace(".", " ")
                .replace(";", " ")
                // Remove math operators
                .replace("+", " ")
                .replace("-", " ")
                .replace("*", " ")
                .replace("/", " ")
                .replace("%", " ")
                .replace("=", " ")
                .replace("<", " ")
                .replace(">", " ");

    }

}
