import java.util.*; 

public class Node {
	private AImove value;
	private ArrayList<Node> children;
	
        public Node()
        {
            this.value=null;
            this.children=null;
        }
	public Node(AImove v)
	{
		this.value=v;
		children=null;
	}
	public Node(AImove v, ArrayList<Node> ch)
	{
		this.value=v;
		this.children=ch;
	}
        public Node(Node n)
        {
            this.value=n.value;
            this.children=n.children;
        }
	public AImove getValue()
	{
		return this.value;
	}
	public void setValue(AImove v)
	{
		this.value=v;
	}
	public void setChildren(ArrayList<Node> ch)
	{
		this.children=ch;
	}
	public ArrayList<Node> getChildren()
	{
		return this.children;
	}
	public void printNode()
	{
		System.out.println(this.value);
	}
	public void printLevel(int lvl,int lvl2)
	{
		if(lvl==lvl2)
			printNode();
		else if(this.getChildren()!=null)
			for(int i=0;i<this.getChildren().size();i++)
				this.getChildren().get(i).printLevel(lvl+1,lvl2);
	}
//	public int printLevel2(int lvl,int lvl2,int sum)
//	{
//		if(lvl==lvl2)
//		{
//			System.out.println("value="+this.getValue());
//			return this.getValue();
//		}
//		else if(this.getChildren()!=null)
//			for(int i=0;i<this.getChildren().size();i++)
//			{
//				sum+=this.getChildren().get(i).printLevel2(lvl+1,lvl2,sum);
//				System.out.println("sum="+sum);
//			}
//		else if (lvl!=lvl2 && this.getChildren()==null) return 0;
//		return sum;
//	}
	public void print()
	{
		if(this.getChildren()==null)
			System.out.println(this.getValue());
		else
		{
			for(int i=0;i<this.getChildren().size();i++)
				this.getChildren().get(i).print();
			this.printNode();
		}
	}
        public void clear()
        {
            this.value=null;
            this.children=null;
        }
}
