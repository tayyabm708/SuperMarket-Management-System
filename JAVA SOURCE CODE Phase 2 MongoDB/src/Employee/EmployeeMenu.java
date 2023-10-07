package Employee;

import Profiling.MainMenu;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Arrays;

public class EmployeeMenu extends JFrame implements ActionListener {
    JButton ShowO,showP,C,Q,R;
    String username;
    public EmployeeMenu(String str){
        username=str;
        // Connect to MongoDB
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("EzShpop");

        try {
            MongoCollection<Document> employeeCollection = database.getCollection("Employee");
            BasicDBObject query = new BasicDBObject("USERNAME", username);
            FindIterable<Document> result = employeeCollection.find(query);

            if (result.iterator().hasNext()) {
                Document employeeDocument = result.iterator().next();

                setTitle(employeeDocument.getString("FIRST_NAME") + " " + employeeDocument.getString("LAST_NAME"));
                setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                setResizable(false);
                setSize(700, 600);
                setLayout(null);
                setLocationRelativeTo(null);
                ImageIcon mainlogo = new ImageIcon("images\\mainlogo.png");
                setIconImage(mainlogo.getImage());

                JPanel head = new JPanel(null);
                head.setBounds(0, 0, 700, 100);
                head.setBackground(new Color(44, 62, 80));
                JLabel heading = new JLabel(employeeDocument.getString("FIRST_NAME") + " " + employeeDocument.getString("LAST_NAME"));
                heading.setFont(new Font("Rooney", Font.BOLD, 40));
                heading.setBounds(15, 25, 300, 50);
                heading.setForeground(Color.white);
                JLabel email = new JLabel(employeeDocument.getString("EMAIL"));
                email.setFont(new Font("Rooney", Font.PLAIN, 16));
                email.setBounds(500, 50, 300, 50);
                email.setForeground(Color.white);

                head.add(heading);
                head.add(email);

                JPanel body = new JPanel(null);
                body.setBounds(0, 100, 700, 500);
                body.setBackground(new Color(213, 245, 227));

                ShowO = new JButton("Approve Orders");
                ShowO.setFocusable(false);
                ShowO.setBounds(250, 50, 200, 65);
                ShowO.setFont(new Font("Bodoni", Font.BOLD, 18));
                ShowO.setBackground(new Color(40, 55, 71));
                ShowO.setForeground(Color.white);
                ShowO.setBorder(BorderFactory.createEtchedBorder());
                ShowO.addActionListener(this);

                showP = new JButton("Manage Stock");
                showP.setFocusable(false);
                showP.setBounds(250, 140, 200, 65);
                showP.setFont(new Font("Bodoni", Font.BOLD, 18));
                showP.setBackground(new Color(40, 55, 71));
                showP.setForeground(Color.white);
                showP.setBorder(BorderFactory.createEtchedBorder());
                showP.addActionListener(this);

                C = new JButton("Complain");
                C.setFocusable(false);
                C.setBounds(250, 230, 200, 65);
                C.setFont(new Font("Bodoni", Font.BOLD, 18));
                C.setBackground(new Color(40, 55, 71));
                C.setForeground(Color.white);
                C.setBorder(BorderFactory.createEtchedBorder());
                C.addActionListener(this);

                Q = new JButton("Queries");
                Q.setFocusable(false);
                Q.setBounds(250, 320, 200, 65);
                Q.setFont(new Font("Bodoni", Font.BOLD, 18));
                Q.setBackground(new Color(40, 55, 71));
                Q.setForeground(Color.white);
                Q.setBorder(BorderFactory.createEtchedBorder());
                Q.addActionListener(this);

                JButton back = new JButton("Log Out");
                back.setForeground(Color.white);
                back.setBackground(new Color(203, 67, 53));
                back.setBounds(570, 400, 90, 40);
                back.setFocusable(false);
                back.setFont(new Font("Tahoma", Font.BOLD, 14));
                back.addActionListener(E -> {
                    new MainMenu();
                    dispose();
                });

                R = new JButton("Resign");
                R.setForeground(Color.white);
                R.setBackground(new Color(175, 96, 26));
                R.setBounds(30, 400, 100, 40);
                R.setFocusable(false);
                R.setFont(new Font("Times New Roman", Font.BOLD, 18));
                R.addActionListener(this);

                body.add(back);
                body.add(ShowO);
                body.add(showP);
                body.add(C);
                body.add(Q);
                body.add(R);

                setVisible(true);
                add(head);
                add(body);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==R){
            // Connect to MongoDB
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
            MongoClient mongoClient = new MongoClient(uri);
            //Retrieve your DB
            MongoDatabase database = mongoClient.getDatabase("EzShpop");
            //Accessing Employee's Collection
            MongoCollection<org.bson.Document> collection = database.getCollection("Employee");
            int opt= JOptionPane.showConfirmDialog(null,"Are u sure you want to Quit Your job??","Confirmation",JOptionPane.YES_NO_OPTION);
            if(opt==0) {
                try {
                    BasicDBObject query = new BasicDBObject("USERNAME", username);
                    collection.deleteOne(query);

                }
                catch (Exception throwables) {
                    throwables.printStackTrace();
                }
                JOptionPane.showMessageDialog(null,"You Quit your Job!!","Good for u mate",JOptionPane.INFORMATION_MESSAGE);
                new MainMenu();
                dispose();
            }
        }
        if(e.getSource()==ShowO){
            new ApproveOrderMenu(username);
            dispose();
        }
        if(e.getSource()==showP){
            String[] C={"Beverages","Bakery Items","Snacks","Fruits & Vegetables"};
            String opt=(String)JOptionPane.showInputDialog(null,"Pick a Category","Choose",JOptionPane.QUESTION_MESSAGE,null,C,C[0]);
            if(opt != null) {
                new ManageStock(opt,username);
                dispose();
            }
        }
        if(e.getSource()==Q) {
            try{
                MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                MongoClient mongoClient = new MongoClient(uri);
                MongoDatabase database = mongoClient.getDatabase("EzShpop");
                // Access the collection
                MongoCollection<Document> collection = database.getCollection("Customer_Query");
                FindIterable<Document> result = collection.find();
                MongoCursor<Document> cursor = result.iterator();

            if (cursor.hasNext()) {
                Object[] options = {"Close", "Delete", "Next"};

                while(cursor.hasNext()){
                    Document document = cursor.next();
                    int id = document.getInteger("QUERY_ID");
                    String msg= document.getString("MSG");
                    String emp = document.getString("CUSTOMER_USERNAME");

                    int opt = JOptionPane.showOptionDialog(null, msg, emp,
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, 0);

                    if (opt == 0 || opt == -1) {
                        break;
                    } else if (opt == 1) {
                        DeleteResult deleteResult = collection.deleteOne(Filters.eq("QUERY_ID", id));
                        if (deleteResult.getDeletedCount() > 0) {
                            JOptionPane.showMessageDialog(null, "Query deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "Failed to delete query", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }

                }

                }
            else
                JOptionPane.showMessageDialog(null, "No Queries Found", "gg", JOptionPane.INFORMATION_MESSAGE);
        }
            catch (Exception throwables){
                throwables.printStackTrace();
            }
        }
        if(e.getSource()==C){
            String msg=JOptionPane.showInputDialog(null,"Enter Message","Complain",JOptionPane.INFORMATION_MESSAGE);
            if(msg!=null){
                // Connect to MongoDB
                MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                MongoClient mongoClient = new MongoClient(uri);
                MongoDatabase database = mongoClient.getDatabase("EzShpop");

                try {
                    MongoCollection<Document> employeeQueryCollection = database.getCollection("Employee_Query");

                    // Retrieve the maximum query_id from the collection
                    Document maxQueryIdDoc = employeeQueryCollection.aggregate(Arrays.asList(
                            Aggregates.group(null, Accumulators.max("QUERY_ID", "$QUERY_ID"))
                    )).first();

                    int id = maxQueryIdDoc != null ? maxQueryIdDoc.getInteger("QUERY_ID", 0) + 1 : 1;

                    // Create a new document for the employee query
                    Document employeeQueryDocument = new Document()
                            .append("MSG", msg)
                            .append("QUERY_ID", id)
                            .append("EMPLOYEE_USERNAME", username);

                    // Insert the document into the employee_query collection
                    employeeQueryCollection.insertOne(employeeQueryDocument);

                    JOptionPane.showMessageDialog(null, "Complain sent to Admin :)", "Karen", JOptionPane.INFORMATION_MESSAGE);

                } catch (MongoException eX) {
                    eX.printStackTrace();
                }

            }
        }
    }
}
