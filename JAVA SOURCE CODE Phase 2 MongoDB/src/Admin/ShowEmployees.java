package Admin;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ShowEmployees extends JFrame implements ActionListener  {
    JButton del;
    JTable Product;
    ShowEmployees(){
        setTitle("Employees");
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
        JLabel heading= new JLabel("Employee Details");
        heading.setFont(new Font("Futura",Font.BOLD,35));
        heading.setBounds(30,25,400,50);
        heading.setForeground(Color.white);
        head.add(heading);

        String[] col={"No.","UserName","Age","Gender","Phone no.","Email"};

        DefaultTableModel Tb=new DefaultTableModel(col,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Product=new JTable(Tb);
        try {
            // Connect to MongoDB Server
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
            MongoClient mongoClient = new MongoClient(uri);
            MongoDatabase database = mongoClient.getDatabase("EzShpop");
            // Access the collection
            MongoCollection<Document> collection = database.getCollection("Employee");

            FindIterable<Document> result = collection.find();
            MongoCursor<Document> cursor = result.iterator();
//            long count = collection.countDocuments();
            int no = 1;

            for (Document document : result) {
//                Document document = cursor.next();
                String field1 = document.getString("USERNAME");
                int field2 = document.getInteger("AGE");
                String field3 = document.getString("GENDER");
                String field4 = document.getString("PHONE_NO");
                String field5 = document.getString("EMAIL");


                Object[] o = {no++, field1, field2, field3, field4, field5};
                Tb.addRow(o);
            }

            cursor.close();
        }
        catch (Exception throwables){
            throwables.printStackTrace();
        }
        Product.setFont(new Font("Futura",Font.PLAIN,14));
        Product.setRowHeight(30);
        TableColumnModel columnModel = Product.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(50);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(150);
        columnModel.getColumn(5).setPreferredWidth(250);



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
        back.addActionListener(En -> {new AdminMenu();dispose();});
        footer.add(back);

        del =new JButton("Fire");
        del.setForeground(Color.white);
        del.setBackground(new Color(230, 126, 34));
        del.setBounds(40,12,90,40);
        del.setFocusable(false);
        del.setFont(new Font("Didot",Font.BOLD,17));
        del.addActionListener(this);
        footer.add(del);


        setVisible(true);
        add(head);
        add(body);
        add(footer);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==del){
            if (Product.getSelectedRow() != -1) {
                int opt=JOptionPane.showConfirmDialog(null,"Are u sure you want to Fire?","Someone's salty",JOptionPane.YES_NO_OPTION);
                if(opt==0) {
                    try {
                        // Connect to MongoDB Server
                        MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
                        MongoClient mongoClient = new MongoClient(uri);
                        MongoDatabase database = mongoClient.getDatabase("EzShpop");
                        // Access the collection
                        MongoCollection<Document> collection = database.getCollection("Employee");

                        String username = Product.getModel().getValueAt(Product.getSelectedRow(), 1).toString();
                        Bson filter = Filters.eq("USERNAME", username);
                        DeleteResult result = collection.deleteOne(filter);

                        if (result.getDeletedCount() > 0) {
                            JOptionPane.showMessageDialog(null, "Employee Fired!!", "Good luck being lonely", JOptionPane.INFORMATION_MESSAGE);
                            new ShowEmployees();
                            dispose();
                        } else {
                            JOptionPane.showMessageDialog(null, "Employee not found", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                    }
                    catch (Exception throwables){
                        throwables.printStackTrace();
                    }
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Select an Employee!!", "Dumb Dumb", JOptionPane.ERROR_MESSAGE);
        }
    }
}
