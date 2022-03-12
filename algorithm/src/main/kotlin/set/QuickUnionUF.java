package set;

/**
 * Quick-union algorithm
 *
 * @author Administrator
 */
public class QuickUnionUF implements UnionFind {

    private int[] id;
    private int count;

    public QuickUnionUF(int n) {
        count = n;
        id = new int[n];
        for (int i = 0; i < n; i++) {
            id[i] = i;
        }
    }

    @Override
    public void union(int p, int q) {
        int pRoot = find(p);
        int qRoot = find(q);

        if (pRoot == qRoot) {
            return;
        }

        id[pRoot] = qRoot;
        count--;
    }

    @Override
    public int find(int p) {
        int root = p;

        while (root != id[root]) {
            root = id[root];
        }

        while (id[p] != root) {
            int temp = p;
            p = id[p];
            id[temp] = root;
        }

        return root;
    }

    @Override
    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    @Override
    public int count() {
        return count;
    }
}
