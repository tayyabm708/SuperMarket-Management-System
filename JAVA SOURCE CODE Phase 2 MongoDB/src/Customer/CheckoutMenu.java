package Customer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class CheckoutMenu extends JFrame implements ActionListener {
    MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
    MongoClient mongoClient = new MongoClient(uri);
    //Retrieve your DB
    MongoDatabase database = mongoClient.getDatabase("EzShpop");
    //Accessing Product's Collection
    MongoCollection<Document> collection = database.getCollection("Product");


    String cat,table;
    double Total;
    ArrayList<Object[]> Cart;
    JButton del,Place;
    JTable Product;
    String username;
    CheckoutMenu(ArrayList<Object[]> C,double t,String c,String str) {
        cat = c;
        Cart=C;
        Total=t;
        username=str;
        setTitle("Chekout");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);
        setSize(700, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        ImageIcon mainlogo = new ImageIcon("images\\mainlogo.png");
        setIconImage(mainlogo.getImage());

        JPanel head = new JPanel();
        head.setBounds(0, 0, 700, 100);
        head.setBackground(new Color(44, 62, 80));
        head.setLayout(null);
        JLabel heading = new JLabel("Items in Cart:");
        heading.setFont(new Font("Futura", Font.BOLD, 35));
        heading.setBounds(30, 25, 400, 50);
        heading.setForeground(Color.white);
        head.add(heading);

        String[] col = {"No.", "Name", "Price (Rs.)", "Quantity", "Total"};

        DefaultTableModel Tb = new DefaultTableModel(col, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Product = new JTable(Tb);
        for (Object[] objects : Cart) {
            Object[] obj={objects[0],objects[1],objects[2],objects[3],objects[4]};
            Tb.addRow(obj);
        }
        Product.setFont(new Font("Futura", Font.PLAIN, 14));
        Product.setRowHeight(30);
        TableColumnModel columnModel = Product.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(300);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(150);


        JScrollPane body = new JScrollPane(Product);
        body.setBounds(1, 100, 687, 300);
        body.setBackground(new Color(234, 250, 241));
        JPanel T=new JPanel(null);
        T.setBounds(0,400,700,100);
        T.setBackground(new Color(208, 211, 212));
        JLabel tname=new JLabel("Total:");
        tname.setForeground(new Color(52, 73, 94));
        tname.setFont(new Font("Rockwell",Font.BOLD,30));
        tname.setBounds(210,20,120,60);
        JLabel total=new JLabel(Total+" Rs.");
        total.setForeground(new Color(40, 180, 99));
        total.setFont(new Font("Myriad",Font.BOLD,30));
        total.setBounds(330,20,150,60);

        T.add(tname);
        T.add(total);



        JPanel footer = new JPanel();
        footer.setBounds(0, 500, 700, 100);
        footer.setBackground(new Color(86, 101, 115));
        footer.setLayout(null);

        JButton back = new JButton("Cancel");
        back.setForeground(Color.white);
        back.setBackground(new Color(203, 67, 53));
        back.setBounds(580, 20, 90, 30);
        back.setFocusable(false);
        back.setFont(new Font("Tahoma", Font.PLAIN, 12));
        back.addActionListener(E -> {
            int opt=JOptionPane.showConfirmDialog(null,"This will remove all items from your cart?","Warning",JOptionPane.OK_CANCEL_OPTION);
            if(opt==0) {
                new ShopMenu(cat, username);
                dispose();
            }
        });
        footer.add(back);

        del = new JButton("Remove");
        del.setForeground(Color.white);
        del.setBackground(new Color(230, 126, 34));
        del.setBounds(30, 20, 90, 30);
        del.setFocusable(false);
        del.setFont(new Font("Didot", Font.PLAIN, 12));
        del.addActionListener(this);
        footer.add(del);

        Place = new JButton("Place Order");
        Place.setForeground(Color.white);
        Place.setBackground(new Color(22, 160, 133));
        Place.setBounds(275, 10, 150, 40);
        Place.setFocusable(false);
        Place.setFont(new Font("Didot", Font.BOLD, 16));
        Place.addActionListener(this);
        footer.add(Place);


        setVisible(true);
        add(head);
        add(body);
        add(T);
        add(footer);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==del){
            int opt=JOptionPane.showConfirmDialog(null,"Remove Item from Cart?","Sure?",JOptionPane.YES_NO_OPTION);
            if(opt==0){
                int t= (int) Cart.get(Product.getSelectedRow())[4];
                Cart.remove(Product.getSelectedRow());
                Total-=t;
                if(Cart.isEmpty()){
                    new ShopMenu(cat, username);
                    dispose();
                }
                else {
                    new CheckoutMenu(Cart, Total, cat, username);
                    dispose();
                }
            }
        }
        if(e.getSource()==Place){
            int opt=JOptionPane.showConfirmDialog(null,"You want to Place Order?","Sure?",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            if(opt==0){
                StringBuilder items= new StringBuilder();
                for(Object[] i:Cart){
                    try {
                        table = switch (cat) {
                            case "Beverages" -> "Beverage";
                            case "Snacks" -> "snack";
                            case "Bakery Items" -> "Bakery_item";
                            default -> "fruit";
                        };
                        Document query = new Document("PRODUCT_ID", i[5]);
                        FindIterable<Document> result = collection.find(query);
                        MongoCursor<Document> cursor = result.iterator();

                        if (cursor.hasNext()) {
                            Document document = cursor.next();
                            int stock = document.getInteger("STOCK");

                            if (stock >= (int) i[3]) {
                                int updatedStock = stock - (int) i[3];

                                Document updateQuery = new Document("$set", new Document("STOCK", updatedStock));
                                collection.updateOne(query, updateQuery);
                            }

                            items.append(i[1]).append(", ");
                        }

                    }
                    catch (Exception throwables){
                        throwables.printStackTrace();
                    }
                }

                try {
                    MongoCollection<Document> ordersCollection = database.getCollection("Orders");

                    int id = 0;
                    Document maxOrderIdDoc = ordersCollection.aggregate(Arrays.asList(
                            Aggregates.group(null, Accumulators.max("ORDER_ID", "$ORDER_ID"))
                    )).first();
                    if (maxOrderIdDoc != null) {
                        id = maxOrderIdDoc.getInteger("ORDER_ID", 0) + 1;
                    }

                    Document orderDocument = new Document()
                            .append("ITEMS", items.toString())
                            .append("TOTAL", Total)
                            .append("STATUS", false)
                            .append("ORDER_ID", id)
                            .append("CUSTOMER_USERNAME", username);

                    ordersCollection.insertOne(orderDocument);

                    JOptionPane.showMessageDialog(null, "Order Placed Successfully", "Mashallah", JOptionPane.INFORMATION_MESSAGE);
                    new CustomerMenu(username);
                    dispose();

                }
                catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
}
