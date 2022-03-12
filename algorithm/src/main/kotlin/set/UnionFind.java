package set;

/**
 * union find algorithm
 *
 * @author ShadowySpirits
 */
public interface UnionFind {

    /**
     * Add connection between p and q
     *
     * @param p object p
     * @param q object q
     */
    void union(int p, int q);

    /**
     * find the component identifier for p
     *
     * @param p object p
     * @return component identifier for p
     */
    int find(int p);

    /**
     * check if q and p are in the same component
     *
     * @param p object p
     * @param q object q
     * @return return true if p and q are in the same component
     */
    boolean connected(int p, int q);

    /**
     * get the number of components
     *
     * @return number of components
     */
    int count();
}
