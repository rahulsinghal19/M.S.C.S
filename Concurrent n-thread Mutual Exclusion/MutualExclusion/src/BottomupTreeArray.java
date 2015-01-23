import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class Node {
	int key;
	Node left;
	Node right;
	Peterson lock;
	
	Node(int key) {
		this.key = key;
		this.left = null;
		this.right = null;
		lock = new Peterson();
	}
}

public class BottomupTreeArray {
	
	int n;
	int[] nArr;
	static ArrayList<Node> Arr = new ArrayList<>();
	
	public BottomupTreeArray(int n) {
		// TODO Auto-generated constructor stub
		this.n = n;
	}

	public ArrayList<Node> TreeToArr(Node root) {
		// TODO Auto-generated method stub
		Queue<Node> q = new LinkedList<>();
		q.add(root);
		while(!q.isEmpty()) {
			Node tmp = q.remove();
			Arr.add(tmp);
			if(tmp.left != null) {
				q.add(tmp.left);
			}
			if(tmp.right != null) {
				q.add(tmp.right);
			}
		}
		return Arr;
	}

	public Node CreateBTree(int[] nArr, int low, int high) {
		// TODO Auto-generated method stub
		if(low > high) 
			return null;
		int mid = (low + high) / 2;
		Node root = new Node(nArr[mid]);
		root.left = CreateBTree(nArr, low, mid-1);
		root.right = CreateBTree(nArr, mid+1, high);
		return root;
	}
	
	
	
}
