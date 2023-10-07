package Admin;

import Profiling.MainMenu;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AdminMenu extends JFrame implements ActionListener {

    JButton AddP,showP,ShowE,Q,R;


    public AdminMenu(){
        // Connect to MongoDB
        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("EzShpop");

        // Access the "admin" collection
        MongoCollection<org.bson.Document> collection = database.getCollection("admin");

        // Retrieve the admin document
        Document adminDocument = (Document) collection.find().first();
        if (adminDocument != null) {
            String firstName = adminDocument.getString("FIRST_NAME");
            String lastName = adminDocument.getString("LAST_NAME");
            String email = adminDocument.getString("EMAIL");

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
            JLabel heading = new JLabel(firstName + " " + lastName);
            heading.setFont(new Font("Rooney", Font.BOLD, 40));
            heading.setBounds(15, 25, 300, 50);
            heading.setForeground(Color.WHITE);
            JLabel emailLabel = new JLabel(email);
            emailLabel.setFont(new Font("Rooney", Font.PLAIN, 16));
            emailLabel.setBounds(500, 50, 300, 50);
            emailLabel.setForeground(Color.WHITE);

            head.add(heading);
            head.add(emailLabel);

            JPanel body = new JPanel(null);
            body.setBounds(0, 100, 700, 500);
            body.setBackground(new Color(213, 245, 227));

            AddP = new JButton("Add Products");
            // Set the properties for AddP button
            AddP.setFocusable(false);
            AddP.setBounds(250,50,200,65);
            AddP.setFont(new Font("Bodoni",Font.BOLD,18));
            AddP.setBackground(new Color(40, 55, 71));
            AddP.setForeground(Color.white);
            AddP.setBorder(BorderFactory.createEtchedBorder());
            AddP.addActionListener(this);

            showP = new JButton("Manage Products");
            // Set the properties for showP button
            showP.setFocusable(false);
            showP.setBounds(250,140,200,65);
            showP.setFont(new Font("Bodoni",Font.BOLD,18));
            showP.setBackground(new Color(40, 55, 71));
            showP.setForeground(Color.white);
            showP.setBorder(BorderFactory.createEtchedBorder());
            showP.addActionListener(this);


            ShowE = new JButton("Manage Employees");
            // Set the properties for ShowE button
            ShowE.setFocusable(false);
            ShowE.setBounds(250,230,200,65);
            ShowE.setFont(new Font("Bodoni",Font.BOLD,18));
            ShowE.setBackground(new Color(40, 55, 71));
            ShowE.setForeground(Color.white);
            ShowE.setBorder(BorderFactory.createEtchedBorder());
            ShowE.addActionListener(this);

            Q = new JButton("Queries");
            // Set the properties for Q button
            Q.setFocusable(false);
            Q.setBounds(250,320,200,65);
            Q.setFont(new Font("Bodoni",Font.BOLD,18));
            Q.setBackground(new Color(40, 55, 71));
            Q.setForeground(Color.white);
            Q.setBorder(BorderFactory.createEtchedBorder());
            Q.addActionListener(this);

            JButton back = new JButton("Log Out");
            // Set the properties for back button
            back.setForeground(Color.white);
            back.setBackground(new Color(203, 67, 53));
            back.setBounds(570,400,90,40);
            back.setFocusable(false);
            back.setFont(new Font("Tahoma",Font.BOLD,14));
            back.addActionListener(E -> {
                new MainMenu();
                dispose();
            });

            R = new JButton("Resign");
            // Set the properties for R button
            R.setForeground(Color.white);
            R.setBackground(new Color(175, 96, 26));
            R.setBounds(30,400,100,40);
            R.setFocusable(false);
            R.setFont(new Font("Times New Roman",Font.BOLD,18));
            R.addActionListener(this);

            body.add(back);
            body.add(AddP);
            body.add(showP);
            body.add(ShowE);
            body.add(Q);
            body.add(R);

            setVisible(true);
            add(head);
            add(body);
            setTitle(firstName+" "+lastName);
        }

// Close the connections
        mongoClient.close();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==R){
            // Connect to MongoDB
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
            MongoClient mongoClient = new MongoClient(uri);
            MongoDatabase database = mongoClient.getDatabase("EzShpop");

            // Access the "admin" collection
            MongoCollection<org.bson.Document> collection = database.getCollection("admin");

            int opt= JOptionPane.showConfirmDialog(null,"Are u sure you want to resign?","Confirmation",JOptionPane.YES_NO_OPTION);
           if(opt==0) {
               try {

                   collection.deleteMany(new Document());

                   JOptionPane.showMessageDialog(null, "You Resigned as an Admin!!", "Get yourself Checked lol", JOptionPane.INFORMATION_MESSAGE);
                   new MainMenu();
                   dispose();
               } catch (Exception ex) {
                   ex.printStackTrace();
               }
               finally {
                   mongoClient.close();
               }
           }

        }
        if(e.getSource()==AddP){
            new ProductEntry();
            dispose();
        }
        if(e.getSource()==showP){
            String[] C={"Beverages","Bakery Items","Snacks","Fruits & Vegetables"};
           String opt=(String)JOptionPane.showInputDialog(null,"Pick a Category","Choose",JOptionPane.QUESTION_MESSAGE,null,C,C[0]);
           if(opt != null) {
               new ShowProducts(opt);
               dispose();
           }
        }
        if(e.getSource()==ShowE){
            new ShowEmployees();
            dispose();
        }
        if(e.getSource()==Q){
            try{
                MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                MongoClient mongoClient = new MongoClient(uri);
                MongoDatabase database = mongoClient.getDatabase("EzShpop");
                // Access the collection
                MongoCollection<Document> collection = database.getCollection("Employee_Query");
                FindIterable<Document> result = collection.find();
                MongoCursor<Document> cursor = result.iterator();

                if (cursor.hasNext()) {
                    Object[] options = {"Close", "Delete", "Next"};

                    while (cursor.hasNext()) {
                        Document document = cursor.next();
                        int id = document.getInteger("QUERY_ID");
                        String msg= document.getString("MSG");
                        String emp = document.getString("EMPLOYEE_USERNAME");

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
                } else {
                    JOptionPane.showMessageDialog(null, "No Queries Found", "Information", JOptionPane.INFORMATION_MESSAGE);
                }
            }catch (Exception throwables){
                throwables.printStackTrace();
            }
        }


    }
}
