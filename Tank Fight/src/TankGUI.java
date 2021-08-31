import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.print.Printable;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.lang.Math;
public class TankGUI extends JFrame implements KeyListener{
	
	
	private int guiHeight=794,guiWidth=980;
	private int borderW=50,borderS=guiHeight-205,borderE=guiWidth-120,borderN=62;
	private JLabel showInfo;
	private BGPanel panel;
	private Tank tank1,tank2;
	
	public TankGUI() {
		// TODO Auto-generated constructor stub
		super("坦克大战游戏");
		this.setSize(guiWidth,guiHeight);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(null);
		this.tank1=new Tank(Color.orange,"orange",200,100,new String[] {"1N.gif","1E.gif","1S.gif","1W.gif"});
		this.tank2=new Tank(new Color(30,200,40),"green",400,200,new String[] {"2N.gif","2E.gif","2S.gif","2W.gif"});
		tank1.lifeBar.setBounds(20,8,guiWidth/2-120,35);
		tank2.lifeBar.setBounds(guiWidth/2+80,8,guiWidth/2-120,35);
		this.showInfo=new JLabel("PK");
		this.showInfo.setHorizontalAlignment(SwingConstants.CENTER);
		this.showInfo.setVerticalAlignment(SwingConstants.CENTER);
		this.showInfo.setBounds(guiWidth/2-160, 8, 300,35);
		this.showInfo.setFont(new Font("SansSerif",Font.BOLD,30));
		JPanel  info=new JPanel();
		info.setBounds(0,0,guiWidth-20,50);
		info.setBackground(Color.LIGHT_GRAY);
		info.setLayout(null);
		info.add(tank1.lifeBar);
		info.add(showInfo);
		info.add(tank2.lifeBar);
		this.getContentPane().add(info);
		
		panel=new BGPanel();
		this.getContentPane().add(panel);
		//panel.setLocation(0, 0);
		panel.setBounds(0, 50, guiWidth-20, guiHeight-100);
		panel.setLayout(null);
		panel.add(tank1.body);
		panel.add(tank2.body);
		
		/*JLabel fJLabel=new JLabel();
		fJLabel.setIcon(ballIcon[0]);
		//fJLabel.setBackground(Color.RED);
		panel.add(fJLabel);*/
		panel.setBackground(Color.gray);
		this.addKeyListener(this);
		this.setVisible(true);
	}
	class BGPanel extends JPanel
	{
		Image im;
		public BGPanel() {
			// TODO Auto-generated constructor stub
			URL url=Panel.class.getResource("image/bg.png");  
			im=Toolkit.getDefaultToolkit().getImage(url);
			//im=Toolkit.getDefaultToolkit().getImage("image/bg.png");//需要注意的是如果用相对路径载入图片,则图片文件必须放在类文件所在文件夹或项目的根文件夹中,否则必须用绝对路径。 
		}
		public BGPanel(Image im) {
			this.im=im;
		}
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			int imwidth=im.getWidth(this);
			int imheight=im.getHeight(this);
			int FWidth=getWidth(); 
			int FHeight=getHeight();//定义窗口的宽度、高度 
			int x=(FWidth-imwidth)/2; 
			int y=(FHeight-imheight)/2;//计算图片的坐标,使图片显示在窗口正中间 
			g.drawImage(im,x,y,null);//绘制图片
		}
		
	}
	class BulletInX
	{
		public BulletInX(int startx,int y,int orienta,Tank tank,Tank target,JPanel obj)
		{
			final int speed=tank.fireSpeed*orienta;
			JLabel label=new JLabel();
			label.setBounds(startx, y, 30, 15);
			if(orienta>0)
				label.setIcon(tank.ballIcon[2]);
			else 
				label.setIcon(tank.ballIcon[0]);
			obj.add(label);
			Timer timer=new Timer();
			timer.schedule(new TimerTask() {
				int x=startx;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					x+=speed;
					label.setLocation(x, y);
					if(x<borderW||x>borderE)
					{
						timer.cancel();
						obj.remove(label);
					}
					if(Math.abs(x-target.x)<target.attackRange&&Math.abs(y-target.y)<target.attackRange)
					{
						target.lifeValue-=tank.spotHurt;
						target.lifeBar.setValue(target.lifeValue);
						if(target.lifeValue<0)
						{
							//Dialog tip = new 
							showInfo.setText(tank.name+" WIN");
							Timer pausetimer=new Timer();
							pausetimer.schedule(new TimerTask() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									showInfo.setText("PK");
									target.lifeValue=target.lifeFull;
									target.lifeBar.setValue(target.lifeFull);
									tank.lifeValue=tank.lifeFull;
									tank.lifeBar.setValue(tank.lifeFull);
								}
									
							},target.resurrection);
							
						}
					}
				}
			}, 0, 100);
		}
	}
	class BulletInY
	{
		public BulletInY(int x,int starty,int orienta,Tank tank,Tank target,JPanel obj)
		{
			final int speed=tank.fireSpeed*orienta;
			JLabel label=new JLabel();
			label.setBounds(x,starty, 15, 30);
			if(orienta>0)
				label.setIcon(tank.ballIcon[3]);
			else 
				label.setIcon(tank.ballIcon[1]);
			obj.add(label);
			Timer timer=new Timer();
			timer.schedule(new TimerTask() {
				int y=starty;
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					y+=speed;
					label.setLocation(x, y);
					if(y>borderS||y<borderN)
					{
						timer.cancel();
						obj.remove(label);
					}
					if(Math.abs(x-target.x)<target.attackRange&&Math.abs(y-target.y)<target.attackRange)
					{
						target.lifeValue-=tank.spotHurt;
						target.lifeBar.setValue(target.lifeValue);
						if(target.lifeValue<0)
						{
							//Dialog tip = new 
							showInfo.setText(tank.name+" WIN");
							Timer pausetimer=new Timer();
							pausetimer.schedule(new TimerTask() {
								
								@Override
								public void run() {
									// TODO Auto-generated method stub
									showInfo.setText("PK");
									target.lifeValue=target.lifeFull;
									target.lifeBar.setValue(target.lifeFull);
									tank.lifeValue=tank.lifeFull;
									tank.lifeBar.setValue(tank.lifeFull);
								}
									
							},target.resurrection);
							
						}
						


					}
					
				}
			}, 0, 100);
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
		//if(tank1.pressed>0)
		if(e.getKeyCode()==65||e.getKeyCode()==87||e.getKeyCode()==68||e.getKeyCode()==83)
		{
			tank1.mt=new Timer();
		tank1.mt.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				switch (tank1.state) {
				case 65:
					new BulletInX(tank1.x, tank1.y+18, -1,tank1, tank2,panel);
					break;
				case 87:
					new BulletInY(tank1.x+18, tank1.y, -1, tank1,tank2, panel);
					break;
				case 68:
					new BulletInX(tank1.x, tank1.y+18, 1,tank1,tank2,  panel);
					break;
				case 83:
					new BulletInY(tank1.x+18, tank1.y, 1,tank1,tank2,  panel);
					break;
				default:
					break;
					}
			}
		}, 300, 500);
		tank1.pressed=false;
		}
			
		//if(tank2.pressed>0)
		if(e.getKeyCode()>36&&e.getKeyCode()<41)
		{
			
			tank2.mt=new Timer();
			tank2.mt.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				switch (tank2.state) {
				case 37:
					new BulletInX(tank2.x, tank2.y+18, -1,tank2, tank1, panel);
					
					break;
				case 38:
					new BulletInY(tank2.x+18, tank2.y, -1, tank2,tank1, panel);
					
					break;
				case 39:
					new BulletInX(tank2.x, tank2.y+18, 1,tank2,tank1,  panel);
					
					break;
				case 40:
					new BulletInY(tank2.x+18, tank2.y, 1,tank2,tank1,  panel);
					break;
				default:
					
					break;
				}
			}
		}, 300, 500);
			tank2.pressed=false;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		//System.out.println(e.getKeyCode()+"");
		if(e.getKeyCode()>36&&e.getKeyCode()<41)
			{
			tank2.pressed=true;
			tank2.state=e.getKeyCode();
			}
		else if(e.getKeyCode()==65||e.getKeyCode()==87||e.getKeyCode()==68||e.getKeyCode()==83)
			{
			tank1.pressed=true;
			tank1.state=e.getKeyCode();
			}
		if(tank1.pressed)
			ui1Update(tank1.state);
		if(tank2.pressed)
			ui2Update(tank2.state);
	}
	public void ui1Update(int keyCode)
	{
		
		switch (keyCode) {
		case 65://A
			tank1.body.setIcon(tank1.bodyIcon[3]);
			
			if(tank1.x-tank1.moveSpeed>borderW)
				{tank1.x-=tank1.moveSpeed;
			tank1.body.setLocation(tank1.x, tank1.y);
			}
			
			break;
		case 87://W
			tank1.body.setIcon(tank1.bodyIcon[0]);
			
			if(tank1.y-tank1.moveSpeed>borderN)
				{tank1.y-=tank1.moveSpeed;
			tank1.body.setLocation(tank1.x, tank1.y);
			}
			
			break;
		case 68://D
			tank1.body.setIcon(tank1.bodyIcon[1]);
			
			{if(tank1.x+tank1.moveSpeed<borderE)
				tank1.x+=tank1.moveSpeed;
			tank1.body.setLocation(tank1.x, tank1.y);
			}
			
			break;
		case 83://S
			tank1.body.setIcon(tank1.bodyIcon[2]);
			
			if(tank1.y+tank1.moveSpeed<borderS)
				{tank1.y+=tank1.moveSpeed;
			tank1.body.setLocation(tank1.x, tank1.y);
			}
			
			break;
		default:
			break;
		}
		tank1.mt.cancel();
		
	}
	public void ui2Update(int keyCode)
	{
		switch (keyCode) {
		case 37:
			tank2.body.setIcon(tank2.bodyIcon[3]);
			//tank2.body.setIcon(ballIcon[0]);
			if(tank2.x-tank2.moveSpeed>borderW)
			{
				tank2.x-=tank2.moveSpeed;
				tank2.body.setLocation(tank2.x, tank2.y);
				
			}
			break;
		case 38:
			tank2.body.setIcon(tank2.bodyIcon[0]);
			
			if(tank2.y-tank2.moveSpeed>borderN)
				{tank2.y-=tank2.moveSpeed;
				tank2.body.setLocation(tank2.x, tank2.y);
				}
			break;
		case 39:
			tank2.body.setIcon(tank2.bodyIcon[1]);
			
			if(tank2.x+tank2.moveSpeed<borderE)
				{tank2.x+=tank2.moveSpeed;
				tank2.body.setLocation(tank2.x, tank2.y);
				}
			break;
		case 40:
			tank2.body.setIcon(tank2.bodyIcon[2]);
			
			if(tank2.y+tank2.moveSpeed<borderS)
				{tank2.y+=tank2.moveSpeed;
			tank2.body.setLocation(tank2.x, tank2.y);
			}
			
			break;
		}
		tank2.mt.cancel();
		
	}
	public static void main(String arg[])
	{
		
		new inits("image/init.jpg",1000);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		
		new TankGUI();
		
	}
}
class inits extends JWindow {
    /**
     * 构造函数
     *
     * @param filename
     *            欢迎屏幕所用的图片
     * @param frame
     *            欢迎屏幕所属的窗体
     * @param waitTime
     *            欢迎屏幕显示的事件
     */
    public inits(String filename, int waitTime) {
        
    	URL url=Panel.class.getResource(filename); 
        // 建立一个标签，标签中显示图片。
        JLabel label = new JLabel(new ImageIcon(url));
        // 将标签放在欢迎屏幕中间
        getContentPane().add(label, BorderLayout.CENTER);
        pack();
        // 获取屏幕的分辨率大小
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 获取标签大小
        Dimension labelSize = label.getPreferredSize();
        // 将欢迎屏幕放在屏幕中间
        setLocation(screenSize.width / 2 - (labelSize.width / 2),
                screenSize.height / 2 - (labelSize.height / 2));
        /*
        // 增加一个鼠标事件处理器，如果用户用鼠标点击了欢迎屏幕，则关闭。
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                setVisible(false);
                dispose();
            }
        });*/
 
        final int pause = waitTime;
        /**
         * Swing线程在同一时刻仅能被一个线程所访问。一般来说，这个线程是事件派发线程（event-dispatching thread）。
         * 如果需要从事件处理（event-handling）或绘制代码以外的地方访问UI，
         * 那么可以使用SwingUtilities类的invokeLater()或invokeAndWait()方法。
         */
        // 关闭欢迎屏幕的线程
        final Runnable closerRunner = new Runnable() {
            public void run() {
                setVisible(false);
                dispose();
            }
        };
        // 等待关闭欢迎屏幕的线程
        Runnable waitRunner = new Runnable() {
            public void run() {
                try {
                    // 当显示了waitTime后，尝试关闭欢迎屏幕
                    Thread.sleep(pause);
                    SwingUtilities.invokeAndWait(closerRunner);
                    // SwingUtilities.invokeLater(closerRunner);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        setVisible(true);
        // 启动等待关闭欢迎屏幕的线程
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}
class Tank
{
	public int width=50,height=50;
	public int moveSpeed = 15,spotHurt=5,fireSpeed=30;//移动速度
	public int x,y,attackRange=50;
	public int lifeFull=100,lifeValue=100;
	public Timer mt;
	public int state=1;
	public boolean pressed=false;
	public JProgressBar lifeBar;
	public ImageIcon bodyIcon[],ballIcon[];;
	public JLabel body;
	public int resurrection=6000;
	public String name="";
	public Tank(Color foreground,String name,int x,int y,String imgString[])
	{
		this.x=x;
		this.y=y;
		this.name=name;
		mt=new Timer();
		lifeBar=new JProgressBar();
		lifeBar.setStringPainted(true);//设置进度条显示提示信息
		lifeBar.setForeground(foreground);
		lifeBar.setFont(new Font("SansSerif",Font.BOLD,20));
		lifeBar.setValue(lifeValue);
		String sbase="image/";
		bodyIcon=new ImageIcon[4];
		for(int i=0;i<4;i++)
		{
			URL url=Panel.class.getResource(sbase+imgString[i]);  
			bodyIcon[i]=new ImageIcon(url);
			bodyIcon[i].setImage(bodyIcon[i].getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
		}
		body=new JLabel();
		body.setIcon(bodyIcon[0]);
		body.setBounds(x, y, width, height);
		ballIcon=new ImageIcon[4];
		String ballname[]= {"ballW.png","ballN.png","ballE.png","ballS.png"};
		for(int i=0;i<ballIcon.length;i++)
		{
			URL url=Panel.class.getResource(sbase+ballname[i]);  
			ballIcon[i]=new ImageIcon(url);
			//ballIcon[i].setImage(ballIcon[i].getImage().getScaledInstance(width, height,Image.SCALE_DEFAULT));
		}
	}
	
}

