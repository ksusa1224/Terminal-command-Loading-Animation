package com.example.demo.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class DemoController {

	private String command = "";
	private String[] result = new String[1];
	private Thread t;
	private Thread it;
	private Runnable r;
	
	@RequestMapping(value={"/index.html","/",""}, method=RequestMethod.GET)
	public String index()
	{
		return "index";
	}

//    public void method(String param1, String param2) {
//        System.out.println("Test method ("+param1+","+param2+")");
//    }

    public String getOutput(String output) {
        System.out.println("Test method ("+ output +")");
        result[0] = output;
        System.out.println("Test method result[0] ("+ result[0] +")");
        return result[0];
    }

	@RequestMapping(value={"/demo.html"})
	public @ResponseBody String demo2(
			@RequestParam(value="command", required=false) String command) {

		String output = "";
//	    it = new Thread(new DemoController().new InnerThread(command));
//	    it.start();
//	    System.out.println(result[0]+"result0");
		try 
		{
			if (command == null || command.equals(""))
			{
				output = "コマンドを入力してください。";
			}
			Process p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
	
			while ((line = buf.readLine()) != null) 
			{
			    output += line + "<br />" +  System.getProperty("line.separator");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			output = "コマンドにエラーがあります。";
		}			
		finally
		{
			try 
			{
				Thread.sleep(3000);
			}
			catch (InterruptedException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			result[0] = output;
//			result[0] = getOutput(output);
//			System.out.println("result[0]:"+result[0]);
		}
	    
	    
		return output;
	}
	
    public class InnerThread extends Thread {

    		private String output = "";
    	
    		InnerThread(String command)
    		{
    			
    		}
    	
		@Override
        public void run() {
//            System.out.println("Test thread");
//            method("A", "B");
			try 
			{
				if (command == null || command.equals(""))
				{
					output = "コマンドを入力してください。";
					return;
				}
				Process p = Runtime.getRuntime().exec(command);
				p.waitFor();
				BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
		
				while ((line = buf.readLine()) != null) 
				{
				    output += line + "<br />" +  System.getProperty("line.separator");
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
				output = "コマンドにエラーがあります。";
			}			
			finally
			{
				try 
				{
					Thread.sleep(3000);
				}
				catch (InterruptedException e) 
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				result[0] = output;
				result[0] = getOutput(output);
				System.out.println("result[0]:"+result[0]);
			}
			
	    }
    }
    
//	@RequestMapping(value={"/demo.html"})
	public @ResponseBody String demo(
			@RequestParam(value="command", required=false) String command) {
		
		this.command = command;
		
	    it = new Thread(new DemoController().new InnerThread(command));
	    it.start();
		// 時間がかかるコマンドを発行した場合の動作確認用
//		it.suspend();
//		try {
//			Thread.sleep(3000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		it.resume();
		System.out.println("result:" + result[0]);
		return result[0];
/*	    
	    
		// 重いので非同期の別スレッドで処理
		t = new Thread(r = new Runnable() 
		{
			private String output;
			
			@Override
		    public void run() 
		    {

				try 
				{
					if (command == null || command.equals(""))
					{
						output = "コマンドを入力してください。";
						return;
					}
					Process p = Runtime.getRuntime().exec(command);
					p.waitFor();
					BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
					String line = "";
			
					while ((line = buf.readLine()) != null) 
					{
					    output += line + "<br />" +  System.getProperty("line.separator");
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					output = "コマンドにエラーがあります。";
				}			
				finally
				{
					System.out.println("output:"+output);
					setResult(output);
					getResult();
					result[0] = output;
					System.out.println("result[0]:"+result[0]);
				}
				
		    }
			
			public void setResult(String output)
			{
				result[0] = output;
			}

			public String getResult()
			{
				return output;
			}

		});
		
		
		System.out.println("result[0]:"+result[0]);
		t.start();
		
		// 時間がかかるコマンドを発行した場合の動作確認用
		t.suspend();
		System.out.println("result[0]:"+result[0]);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("result[0]:"+result[0]);
		t.resume();
		
	*/
		
//		if (output == null || output.equals(""))
//		{
//			output = "コマンドにエラーがあります。";
//		}
//		System.out.println("result:" + result[0]);
//		return result[0];
	}
}
