package Customer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;

public class ShopMenu extends JFrame implements ActionListener {
    MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
    MongoClient mongoClient = new MongoClient(uri);
    //Retrieve your DB
    MongoDatabase database = mongoClient.getDatabase("EzShpop");
    //Accessing Product's Collection
    MongoCollection<Document> collection = database.getCollection("Product");

    JButton Add,check;
    String cat,username,table,Pid;
    JTable Product;
    double Total=0;
    int no=1;
    ArrayList<Object[]> Cart= new ArrayList<>();
    ShopMenu(String c,String str){
        cat=c;
        username=str;
        setTitle("Product");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(700,600);
        setLayout(null);
        setLocationRelativeTo(null);
        ImageIcon mainlogo=new ImageIcon("images\\mainlogo.png");
        setIconImage(mainlogo.getImage());

        JPanel head=new JPanel();
        head.setBounds(0,0,700,100);
        head.setBackground(new Color(44, 62, 80));
        head.setLayout(null);
        JLabel heading= new JLabel(cat);
        heading.setFont(new Font("Futura",Font.BOLD,35));
        heading.setBounds(30,25,400,50);
        heading.setForeground(Color.white);
        head.add(heading);

        String[] col={"No.","Preview","Name","Price(Rs.)","Quantity"};

        DefaultTableModel Tb=new DefaultTableModel(col,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            public Class getColumnClass(int column)
            {
                return getValueAt(0, column).getClass();
            }
        };
        Product=new JTable(Tb);
        table = switch (cat) {
            case "Beverages" -> "Beverage";
            case "Snacks" -> "snack";
            case "Bakery Items" -> "Bakery_item";
            default -> "fruit";
        };
        try {
            Document query = new Document("CATEGORY",cat);
            FindIterable<Document> result = collection.find(query);
            MongoCursor<Document> cursor = result.iterator();


            int no = 1;
            for(Document document : result) {
                ImageIcon img=new ImageIcon(new ImageIcon(document.getString("IMG")).getImage().getScaledInstance(190,190,Image.SCALE_DEFAULT));
                Object[] o = {no++,img, document.getString("NAME") +" ("+ document.getString("PRODUCT_ID")
                        +")", document.getInteger("PRICE"), document.getInteger("STOCK")};
                Tb.addRow(o);
            }
        }
        catch (Exception throwables){
            throwables.printStackTrace();
        }
        Product.setFont(new Font("Futura",Font.PLAIN,15));
        Product.setRowHeight(200);
        TableColumnModel columnModel = Product.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(200);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(70);
        columnModel.getColumn(4).setPreferredWidth(70);



        JScrollPane body=new JScrollPane(Product);
        body.setBounds(1,100,687,400);
        body.setBackground(new Color(234, 250, 241 ));


        JPanel footer=new JPanel();
        footer.setBounds(0,500,700,400);
        footer.setBackground(new Color(93, 109, 126));
        footer.setLayout(null);

        JButton back =new JButton("Back");
        back.setForeground(Color.white);
        back.setBackground(new Color(203, 67, 53));
        back.setBounds(30,20,70,30);
        back.setFocusable(false);
        back.setFont(new Font("Tahoma",Font.PLAIN,12));
        back.addActionListener(E -> {new CustomerMenu(username);dispose();});
        footer.add(back);

        check =new JButton("Checkout");
        check.setForeground(Color.white);
        check.setBackground(new Color(230, 126, 34));
        check.setBounds(570,11,100,35);
        check.setFocusable(false);
        check.setFont(new Font("Didot",Font.BOLD,13));
        check.addActionListener(this);
        footer.add(check);

        Add =new JButton("Add to Cart");
        Add.setForeground(Color.white);
        Add.setBackground(new Color(22, 160, 133));
        Add.setBounds(275,10,150,40);
        Add.setFocusable(false);
        Add.setFont(new Font("Didot",Font.BOLD,16));
        Add.addActionListener(this);
        footer.add(Add);


        setVisible(true);
        add(head);
        add(body);
        add(footer);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==Add){
            if(Product.getSelectedRow()!=-1) {
                String q=JOptionPane.showInputDialog(null,"Enter Amount:","Quantity",JOptionPane.QUESTION_MESSAGE);
                if(q!=null){
                    try{
                        int Q=Integer.parseInt(q);
                        String s=Product.getModel().getValueAt(Product.getSelectedRow(),2).toString();
                        int si=s.indexOf('(');
                        int ei=s.indexOf(')');
                        Pid=s.substring(si+1,ei);
                        try {
                            Document query = new Document("PRODUCT_ID", Pid);
                            FindIterable<Document> result = collection.find(query);
                            MongoCursor<Document> cursor = result.iterator();

                            if (cursor.hasNext()) {
                                Document document = cursor.next();
                                int stock = document.getInteger("STOCK");
                                if (stock - Q >= 0) {
                                    Object[] item = {no++, document.getString("NAME"), document.getInteger("PRICE"), Q, document.getInteger("PRICE") * Q, Pid};
                                    for (Object[] objects : Cart) {
                                        if (objects[1].equals(item[1])) {
                                            JOptionPane.showMessageDialog(null, "Item Already Added!", "Redundancy", JOptionPane.INFORMATION_MESSAGE);
                                            return;
                                        }
                                    }
                                    Cart.add(item);
                                    Total += document.getInteger("PRICE") * Q;
                                    JOptionPane.showMessageDialog(null, "Item Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                                    Product.removeRowSelectionInterval(0, 0);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Not enough Stock!", "Complain", JOptionPane.WARNING_MESSAGE);
                                }
                            }

                        }
                        catch (Exception throwables){
                            throwables.printStackTrace();
                        }
                    }
                    catch (NumberFormatException num){
                        JOptionPane.showMessageDialog(null,"Enter a Valid Number","Dumb 101",JOptionPane.ERROR_MESSAGE);
                    }
                }

            }
            else
                JOptionPane.showMessageDialog(null,"Select a Product to Add","Bruh..",JOptionPane.ERROR_MESSAGE);
        }
        if(e.getSource()==check){
            int opt=JOptionPane.showConfirmDialog(null,"Go to checkout?","Confirm",JOptionPane.YES_NO_OPTION);
            if(opt==0){
                if(!Cart.isEmpty()) {
                    new CheckoutMenu(Cart, Total, cat, username);
                    dispose();
                }
                else
                    JOptionPane.showMessageDialog(null,"No items in Cart","Lol",JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
