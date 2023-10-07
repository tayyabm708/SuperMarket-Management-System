package Customer;

import Profiling.MainMenu;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

public class CustomerMenu extends JFrame implements ActionListener {

    JButton Shop,Orders,Complain,R;
    String username;
    public CustomerMenu(String str){
        username=str;
        // Connect to MongoDB
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("EzShpop");
        try {
            MongoCollection<Document> employeeCollection = database.getCollection("Customer");
            BasicDBObject query = new BasicDBObject("USERNAME", username);
            FindIterable<Document> result = employeeCollection.find(query);

            if (result.iterator().hasNext()) {
                Document customerDocument = result.iterator().next();

                setTitle(customerDocument.getString("USERNAME"));
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                setResizable(false);
                setSize(600, 500);
                setLayout(null);
                setLocationRelativeTo(null);
                ImageIcon mainlogo = new ImageIcon("images\\mainlogo.png");
                setIconImage(mainlogo.getImage());

                JPanel head = new JPanel(null);
                head.setBounds(0, 0, 600, 100);
                head.setBackground(new Color(44, 62, 80));
                JLabel heading = new JLabel(customerDocument.getString("FIRST_NAME") + " " + customerDocument.getString("LAST_NAME"));
                heading.setFont(new Font("Rooney", Font.BOLD, 30));
                heading.setBounds(15, 25, 200, 50);
                heading.setForeground(Color.white);
                JLabel email = new JLabel(customerDocument.getString("EMAIL"));
                email.setFont(new Font("Rooney", Font.PLAIN, 16));
                email.setBounds(400, 50, 300, 50);
                email.setForeground(Color.white);

                head.add(heading);
                head.add(email);


                JPanel body = new JPanel(null);
                body.setBounds(0, 100, 600, 400);
                body.setBackground(new Color(213, 245, 227));

                Shop = new JButton("Shop");
                Shop.setFocusable(false);
                Shop.setBounds(200, 50, 200, 65);
                Shop.setFont(new Font("Bodoni", Font.BOLD, 18));
                Shop.setBackground(new Color(40, 55, 71));
                Shop.setForeground(Color.white);
                Shop.setBorder(BorderFactory.createEtchedBorder());
                Shop.addActionListener(this);

                Orders = new JButton("Check Orders");
                Orders.setFocusable(false);
                Orders.setBounds(200, 130, 200, 65);
                Orders.setFont(new Font("Bodoni", Font.BOLD, 18));
                Orders.setBackground(new Color(40, 55, 71));
                Orders.setForeground(Color.white);
                Orders.setBorder(BorderFactory.createEtchedBorder());
                Orders.addActionListener(this);

                Complain = new JButton("Complain");
                Complain.setFocusable(false);
                Complain.setBounds(200, 210, 200, 65);
                Complain.setFont(new Font("Bodoni", Font.BOLD, 18));
                Complain.setBackground(new Color(40, 55, 71));
                Complain.setForeground(Color.white);
                Complain.setBorder(BorderFactory.createEtchedBorder());
                Complain.addActionListener(this);


                JButton back = new JButton("Log Out");
                back.setForeground(Color.white);
                back.setBackground(new Color(203, 67, 53));
                back.setBounds(470, 300, 90, 40);
                back.setFocusable(false);
                back.setFont(new Font("Tahoma", Font.BOLD, 14));
                back.addActionListener(E -> {
                    new MainMenu();
                    dispose();
                });

                R = new JButton("Delete Acc");
                R.setForeground(Color.white);
                R.setBackground(new Color(175, 96, 26));
                R.setBounds(30, 300, 100, 40);
                R.setFocusable(false);
                R.setFont(new Font("Tahoma", Font.BOLD, 12));
                R.addActionListener(this);

                body.add(back);
                body.add(Shop);
                body.add(Orders);
                body.add(Complain);
                body.add(R);

                setVisible(true);
                add(head);
                add(body);
            }
        }
        catch (Exception throwables){
            throwables.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==R) {
            // Connect to MongoDB
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
            MongoClient mongoClient = new MongoClient(uri);
            //Retrieve your DB
            MongoDatabase database = mongoClient.getDatabase("EzShpop");
            //Accessing Employee's Collection
            MongoCollection<org.bson.Document> collection = database.getCollection("Customer");

            int opt = JOptionPane.showConfirmDialog(null, "Are u sure you want to Delete your Account?", "Confirmation", JOptionPane.YES_NO_OPTION);
            if (opt == 0) {
                try {
                    BasicDBObject query = new BasicDBObject("USERNAME", username);
                    collection.deleteOne(query);
                }
                catch (Exception throwables) {
                    throwables.printStackTrace();
                }
                JOptionPane.showMessageDialog(null, "Account Deleted!!", "Why._.", JOptionPane.INFORMATION_MESSAGE);
                new MainMenu();
                dispose();
            }
        }
        if(e.getSource()==Shop){
            String[] C={"Beverages","Bakery Items","Snacks","Fruits & Vegetables"};
            String opt=(String)JOptionPane.showInputDialog(null,"Pick a Category","Choose",JOptionPane.QUESTION_MESSAGE,null,C,C[0]);
            if(opt != null) {
                new ShopMenu(opt,username);
                dispose();
            }
        }
        if(e.getSource()== Orders) {
            // Connect to MongoDB
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
            MongoClient mongoClient = new MongoClient(uri);
            //Retrieve your DB
            MongoDatabase database = mongoClient.getDatabase("EzShpop");
            //Accessing Employee's Collection
            MongoCollection<org.bson.Document> ordersCollection = database.getCollection("Orders");
            try {
                Document query = new Document("CUSTOMER_USERNAME", username);
                FindIterable<Document> result = ordersCollection.find(query);

                if (result.iterator().hasNext()) {
                    new OrdersMenu(username);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No orders Found", "Bruh", JOptionPane.WARNING_MESSAGE);
                }

            }
            catch (MongoException throwables){
                    throwables.printStackTrace();
                    }
        }
        if(e.getSource()==Complain){
            String msg=JOptionPane.showInputDialog(null,"Enter Message","Complain",JOptionPane.INFORMATION_MESSAGE);
            if(msg!=null){
                // Connect to MongoDB
                MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                MongoClient mongoClient = new MongoClient(uri);
                MongoDatabase database = mongoClient.getDatabase("EzShpop");

                try {
                    MongoCollection<Document> customerQueryCollection = database.getCollection("Customer_Query");

                    // Retrieve the maximum query_id from the collection
                    Document maxQueryIdDoc = customerQueryCollection.aggregate(Arrays.asList(
                            Aggregates.group(null, Accumulators.max("QUERY_ID", "$QUERY_ID"))
                    )).first();

                    int id = maxQueryIdDoc != null ? maxQueryIdDoc.getInteger("QUERY_ID", 0) + 1 : 1;

                    // Create a new document for the customet query
                    Document customerQueryDocument = new Document()
                            .append("MSG", msg)
                            .append("QUERY_ID", id)
                            .append("CUSTOMER_USERNAME", username);

                    // Insert the document into the customer_query collection
                    customerQueryCollection.insertOne(customerQueryDocument);

                    JOptionPane.showMessageDialog(null, "Complain sent to Employee :)", "Karen", JOptionPane.INFORMATION_MESSAGE);


                }
                catch (Exception throwables) {
                    throwables.printStackTrace();
                }
            }
        }
    }
}
