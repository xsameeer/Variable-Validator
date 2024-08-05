package cmsc256;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface ProgramParserInterface {
    
    public String getJavaFileName();
    
    public void setJavaFile(String javaFileName);
    
    public String getKeywordFileName();
    
    public void setKeywordFile(String keywordFileName);
    
    public AVLTree<String> createKeywordTree() throws FileNotFoundException;
    
    public String getInorderTraversal() throws FileNotFoundException;
    
    public String getPreorderTraversal() throws FileNotFoundException;
    
    public String getPostorderTraversal() throws FileNotFoundException;
    
    public default Map<String, Integer> getValidJavaIdentifiers() throws FileNotFoundException{
        return null;
    }
    

    public default Map<String, List<Integer>> getInvalidJavaIdentifiers(){
        return null;
    }
    
}
