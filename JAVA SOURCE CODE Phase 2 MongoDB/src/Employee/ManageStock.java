package Employee;

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

public class ManageStock extends JFrame implements ActionListener {
    MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
    MongoClient mongoClient = new MongoClient(uri);
    //Retrieve your DB
    MongoDatabase database = mongoClient.getDatabase("EzShpop");
    //Accessing Product's Collection
    MongoCollection<Document> collection = database.getCollection("Product");

    String cat;
    JButton Edit;
    JTable Product;
    String username,table;
    ManageStock(String c,String str){
        username=str;
        cat=c;
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

        String[] col={"No.","ID","Name","Price(Rs.)","Quantity"};

        DefaultTableModel Tb=new DefaultTableModel(col,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 4) {
                    return true;
                }
                return false;
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
            for (Document document : result) {
//                Document document = cursor.next();
                String field1 = document.getString("PRODUCT_ID");
                String field2 = document.getString("NAME");
                int field3 = document.getInteger("PRICE");
                int field4 = document.getInteger("STOCK");


                Object[] o = {no++, field1, field2, field3, field4};
                Tb.addRow(o);
            }

        }
        catch (Exception throwables){
            throwables.printStackTrace();
        }
        Product.setFont(new Font("Futura",Font.PLAIN,15));
        Product.setRowHeight(30);
        TableColumnModel columnModel = Product.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(300);
        columnModel.getColumn(3).setPreferredWidth(95);
        columnModel.getColumn(4).setPreferredWidth(97);



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
        back.setBounds(600,20,60,30);
        back.setFocusable(false);
        back.setFont(new Font("Tahoma",Font.PLAIN,12));
        back.addActionListener(E -> {new EmployeeMenu(username);dispose();});
        footer.add(back);

        Edit =new JButton("Update");
        Edit.setForeground(Color.white);
        Edit.setBackground(new Color(22, 160, 133));
        Edit.setBounds(300,10,100,40);
        Edit.setFocusable(false);
        Edit.setFont(new Font("Didot",Font.BOLD,16));
        Edit.addActionListener(this);
        footer.add(Edit);


        setVisible(true);
        add(head);
        add(body);
        add(footer);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==Edit){
            int rows=0;
            boolean flag=true;
            try {
                MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                MongoClient mongoClient = new MongoClient(uri);
                //Retrieve your DB
                MongoDatabase database = mongoClient.getDatabase("EzShpop");
                //Accessing Product's Collection
                MongoCollection<Document> collection = database.getCollection("Product");

                Document query = new Document("CATEGORY",cat);
                FindIterable<Document> result = collection.find(query);
                MongoCursor<Document> cursor = result.iterator();

                int count = 0;
                for (Document document : result) {
                    count++;
                }

                for (int i = 0; i < count; i++) {
                    try {
                        int Stock = Integer.parseInt(String.valueOf(Product.getModel().getValueAt(i, 4)));
                        try {
                            // Update the document in the collection
                            Document updateQuery = new Document("PRODUCT_ID", String.valueOf(Product.getModel().getValueAt(i, 1)));
                            Document update = new Document("$set", new Document("STOCK", Stock));
                            collection.updateOne(updateQuery, update);
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    catch (NumberFormatException Num) {
                        JOptionPane.showMessageDialog(null, "Enter a Number in row " + (i + 1), "Error 101", JOptionPane.ERROR_MESSAGE);
                        flag = false;
                    }
                }
            }
            catch (Exception throwables){
                throwables.printStackTrace();
            }
            if(flag){
                JOptionPane.showMessageDialog(null,"Stocks Updated Sucesfully!","Add More Cocomo",JOptionPane.INFORMATION_MESSAGE);
                new ManageStock(cat,username);
                dispose();
            }
        }
    }
}
