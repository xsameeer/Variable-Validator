package cmsc256;
/**
 * Implements an AVL tree.
 */
public class AVLTree<T extends Comparable<? super T>> {
    private AVLNode<T> root;

    public AVLTree( ){
        root = null;
    }

    public AVLNode<T> getRoot(){
        return root;
    }

    /**
     * Insert into the tree; duplicates are ignored.
     * @param x the item to insert.
     */
    public void insert(T x) {
        root = insert( x, root );
    }

    /**
     * Remove from the tree. Nothing is done if x is not found.
     * @param x the item to remove.
     */
    public void remove(T x) {
        throw new UnsupportedOperationException( "remove not unimplemented" );
    }

    /**
     * Find the smallest item in the tree.
     * @return smallest item or null if empty.
     */
    public T findMin( ) {
        return elementAt(findMin(root));
    }

    /**
     * Find the largest item in the tree.
     * @return the largest item of null if empty.
     */
    public T findMax( ) {
        return elementAt(findMax(root));
    }

    /**
     * Find an item in the tree.
     * @param x the item to search for.
     * @return the matching item or null if not found.
     */
    public T find(T x) {
        return elementAt(find( x, root));
    }

    /**
     * Make the tree logically empty.
     */
    public void makeEmpty( ) {
        root = null;
    }

    /**
     * Test if the tree is logically empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty( ) {
        return root == null;
    }

    /**
     * Print the tree contents in sorted order.
     */
    public void printTree( ) {
        if(isEmpty())
            System.out.println( "Empty tree" );
        else
            printTree( root );
    }

    /**
     * Internal method to get element field.
     * @param t the node.
     * @return the element field or null if t is null.
     */
    private T  elementAt(AVLNode<T> t) {
        return t == null ? null : t.getElement();
    }

    /**
     * Internal method to insert into a subtree.
     * @param x the item to insert.
     * @param t the node that roots the tree.
     * @return the new root.
     */
    private AVLNode<T> insert(T x, AVLNode<T> t) {
        if( t == null )
            t = new AVLNode<T>( x, null, null );
        else if( x.compareTo(t.getElement()) < 0 ) {
            t.setLeft( insert( x, t.getLeft() ));
            if( height( t.getLeft() ) - height( t.getRight() ) == 2 )
                if( x.compareTo( t.getLeft().getElement() ) < 0 )
                    t = rotateWithLeftChild( t );
                else
                    t = doubleWithLeftChild( t );
        }
        else if( x.compareTo( t.getElement() ) > 0 ) {
            t.setRight(insert( x, t.getRight()));
            if( height( t.getRight() ) - height( t.getLeft() ) == 2 )
                if( x.compareTo( t.getRight().getElement() ) > 0 )
                    t = rotateWithRightChild( t );
                else
                    t = doubleWithRightChild( t );
        }
        else {
            // Duplicate; do nothing
        }
        t.setHeight(max( height( t.getLeft() ), height( t.getRight() ) ) + 1);
        return t;
    }

    /**
     * Internal method to find the largest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the largest item.
     */
    private AVLNode<T> findMax(AVLNode<T> t){
        if( t == null ) return t;

        while( t.getRight() != null )
            t = t.getRight();
        return t;
    }

    /**
     * Internal method to find the smallest item in a subtree.
     * @param t the node that roots the tree.
     * @return node containing the smallest item.
     */
    private AVLNode<T> findMin(AVLNode<T> t){
        if( t == null ) return t;

        while( t.getLeft() != null )
            t = t.getLeft();
        return t;
    }

    /**
     * Internal method to find an item in a subtree.
     * @param x is item to search for.
     * @param t the node that roots the tree.
     * @return node containing the matched item.
     */
    private  AVLNode<T> find( T x, AVLNode<T> t) {
        while( t != null )
            if( x.compareTo( t.getElement() ) < 0 )
                t = t.getLeft();
            else if( x.compareTo( t.getElement() ) > 0 )
                t = t.getRight();
            else
                return t;    // Match

        return null;   // No match
    }

    /**
     * Internal method to print a subtree in sorted order.
     * @param t the node that roots the tree.
     */
    private void printTree( AVLNode t ) {
        if( t != null ) {
            printTree( t.getLeft() );
            System.out.println( t.getElement() );
            printTree( t.getRight() );
        }
    }

    /**
     * Return the height of node t, or -1, if null.
     */
    private int height( AVLNode t ) {
        if(t == null)
            return -1;
        return t.getHeight();
    }

    /**
     * Return maximum of lhs and rhs.
     */
    private int max( int lhs, int rhs ) {
        if(lhs > rhs)
            return lhs;
        return rhs;
    }

    /**
     * Rotate binary tree node with left child.
     * For AVL trees, this is a single rotation for case 1.
     * Update heights, then return new root.
     */
    private  AVLNode<T> rotateWithLeftChild( AVLNode<T> k2 ) {
        AVLNode<T> k1 = k2.getLeft();
        k2.setLeft(k1.getRight());
        k1.setRight(k2);
        k2.setHeight(max(height(k2.getLeft()), height(k2.getRight())) + 1);
        k1.setHeight(max( height( k1.getLeft() ), k2.getHeight() ) + 1);
        return k1;
    }

    /**
     * Rotate binary tree node with right child.
     * For AVL trees, this is a single rotation for case 4.
     * Update heights, then return new root.
     */
    private AVLNode<T> rotateWithRightChild( AVLNode<T> k1 ) {
        AVLNode<T> k2 = k1.getRight();
        k1.setRight(k2.getLeft());
        k2.setLeft(k1);
        k1.setHeight(max(height(k1.getLeft()), height(k1.getRight())) + 1);
        k2.setHeight(max( height( k2.getRight() ), k1.getHeight() ) + 1);
        return k2;
    }

    /**
     * Double rotate binary tree node: first left child
     * with its right child; then node k3 with new left child.
     * For AVL trees, this is a double rotation for case 2.
     * Update heights, then return new root.
     */
    private AVLNode<T> doubleWithLeftChild( AVLNode<T> k3 ) {
        k3.setLeft(rotateWithRightChild(k3.getLeft()));
        return rotateWithLeftChild( k3 );
    }

    /**
     * Double rotate binary tree node: first right child
     * with its left child; then node k1 with new right child.
     * For AVL trees, this is a double rotation for case 3.
     * Update heights, then return new root.
     */
    private AVLNode<T> doubleWithRightChild( AVLNode<T> k1 ) {
        k1.setRight(rotateWithLeftChild(k1.getRight()));
        return rotateWithRightChild( k1 );
    }
    public class AVLNode<T extends Comparable<? super T>> {
        private T element;
        private AVLNode<T> left;
        private AVLNode<T> right;
        private int height;

        public AVLNode(T theElement)  {
            this( theElement, null, null );
        }

        public AVLNode(T theElement, AVLNode<T> lt, AVLNode<T> rt ) {
            element  = theElement;
            left     = lt;
            right    = rt;
            height   = 0;
        }
        public T getElement() {
            return element;
        }

        public void setElement(T el) {
            this.element = el;
        }

        public AVLNode<T> getLeft() {
            return left;
        }

        public void setLeft(AVLNode<T> lft) {
            this.left = lft;
        }

        public AVLNode<T> getRight() {
            return right;
        }

        public void setRight(AVLNode<T> rt) {
            this.right = rt;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
    }
}
