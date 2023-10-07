package Customer;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class OrdersMenu extends JFrame implements ActionListener {
    // Connect to MongoDB
    MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017");
    MongoClient mongoClient = new MongoClient(uri);
    //Retrieve your DB
    MongoDatabase database = mongoClient.getDatabase("EzShpop");
    //Accessing Employee's Collection
    MongoCollection<Document> ordersCollection = database.getCollection("Orders");

    JTable Product,SProduct;
    JButton det,cancel;
    String username;

    OrdersMenu(String str){
        username=str;
        setTitle("Orders");
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
        JLabel heading= new JLabel("Placed Orders");
        heading.setFont(new Font("Futura",Font.BOLD,35));
        heading.setBounds(30,25,400,50);
        heading.setForeground(Color.white);
        head.add(heading);

        String[] col={"No.","Order ID","Items","Total (Rs.)","Shipping Status"};

        DefaultTableModel Tb=new DefaultTableModel(col,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableModel Stb=new DefaultTableModel(col,0){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        Product=new JTable(Tb);
        SProduct=new JTable(Stb);
        try {
            Document query = new Document("CUSTOMER_USERNAME", username);
            FindIterable<Document> result = ordersCollection.find(query);

            int no = 1;
            for (Document doc : result) {
                boolean status = doc.getBoolean("STATUS");
                int orderNumber = doc.getInteger("ORDER_ID");
                double total = doc.getDouble("TOTAL");
                String items = doc.getString("ITEMS");

                if (!status) {
                    Object[] obj = {no, orderNumber, items, total, "Pending"};
                    Tb.addRow(obj);
                } else {
                    Object[] obj = {no, orderNumber, items, total, "Shipped"};
                    Stb.addRow(obj);
                }

                no++;
            }

        }
        catch (Exception throwables) {
            throwables.printStackTrace();
        }
        Product.setFont(new Font("Futura",Font.PLAIN,15));
        Product.setRowHeight(30);
        TableColumnModel columnModel = Product.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(40);
        columnModel.getColumn(1).setPreferredWidth(80);
        columnModel.getColumn(2).setPreferredWidth(400);
        columnModel.getColumn(3).setPreferredWidth(80);
        columnModel.getColumn(4).setPreferredWidth(100);

        SProduct.setFont(new Font("Futura",Font.PLAIN,15));
        SProduct.setRowHeight(30);
        TableColumnModel columModel = SProduct.getColumnModel();
        columModel.getColumn(0).setPreferredWidth(40);
        columModel.getColumn(1).setPreferredWidth(80);
        columModel.getColumn(2).setPreferredWidth(400);
        columModel.getColumn(3).setPreferredWidth(80);
        columModel.getColumn(4).setPreferredWidth(100);



        JScrollPane body=new JScrollPane(Product);
        body.setBounds(1,100,687,150);
        body.setBackground(new Color(234, 250, 241 ));

        JPanel head1=new JPanel();
        head1.setBounds(0,250,700,100);
        head1.setBackground(new Color(44, 62, 80));
        head1.setLayout(null);
        JLabel heading1= new JLabel("Shipped Orders");
        heading1.setFont(new Font("Futura",Font.BOLD,35));
        heading1.setBounds(30,25,400,50);
        heading1.setForeground(Color.white);
        head1.add(heading1);

        JScrollPane body1=new JScrollPane(SProduct);
        body1.setBounds(1,350,687,150);
        body1.setBackground(new Color(234, 250, 241 ));


        JPanel footer=new JPanel();
        footer.setBounds(0,500,700,400);
        footer.setBackground(new Color(93, 109, 126));
        footer.setLayout(null);

        JButton back =new JButton("Back");
        back.setForeground(Color.white);
        back.setBackground(new Color(203, 67, 53));
        back.setBounds(600,20,70,30);
        back.setFocusable(false);
        back.setFont(new Font("Tahoma",Font.PLAIN,12));
        back.addActionListener(E -> {new CustomerMenu(username);dispose();});
        footer.add(back);

        det =new JButton("Details");
        det.setForeground(Color.white);
        det.setBackground(new Color(230, 126, 34));
        det.setBounds(30,20,70,30);
        det.setFocusable(false);
        det.setFont(new Font("Didot",Font.PLAIN,12));
        det.addActionListener(this);
        footer.add(det);

        cancel =new JButton("Cancel Order");
        cancel.setForeground(Color.white);
        cancel.setBackground(new Color(22, 160, 133));
        cancel.setBounds(270,10,150,40);
        cancel.setFocusable(false);
        cancel.setFont(new Font("Didot",Font.BOLD,16));
        cancel.addActionListener(this);
        footer.add(cancel);


        setVisible(true);
        add(head);
        add(body);
        add(footer);
        add(head1);
        add(body1);

    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==cancel) {
            if (Product.getSelectedRow() != -1) {
                int opt = JOptionPane.showConfirmDialog(null, "Cancel Order?", "Sure?", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (opt == 0) {
                    try {
                        Document query = new Document("ORDER_ID", Product.getModel().getValueAt(Product.getSelectedRow(), 1));
                        FindIterable<Document> result = ordersCollection.find(query);

                        ordersCollection.deleteOne(query);
                        JOptionPane.showMessageDialog(null, "Order Cancelled", "Disappointed", JOptionPane.INFORMATION_MESSAGE);
                        new OrdersMenu(username);
                        dispose();
                    }
                    catch (Exception throwables) {
                        throwables.printStackTrace();
                    }
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Select a Non Shipped Order", "Shipped", JOptionPane.WARNING_MESSAGE);
        }
        if(e.getSource()==det){
            if(Product.getSelectedRow()!=-1){
                JOptionPane.showMessageDialog(null,Product.getModel().getValueAt(Product.getSelectedRow(),2),"ITEMS",JOptionPane.INFORMATION_MESSAGE);
                Product.removeRowSelectionInterval(0,Product.getSelectedRow());
            }
            else if(SProduct.getSelectedRow()!=-1){
                JOptionPane.showMessageDialog(null,SProduct.getModel().getValueAt(SProduct.getSelectedRow(),2),"ITEMS",JOptionPane.INFORMATION_MESSAGE);
                SProduct.removeRowSelectionInterval(0,SProduct.getSelectedRow());
            }
            else
                JOptionPane.showMessageDialog(null,"Select an Order to Show Details","Dumb 101",JOptionPane.ERROR_MESSAGE);

        }

    }
}
