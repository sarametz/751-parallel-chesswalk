package cz.hejl.chesswalk;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.StatFs;

public class CrashReporter implements Thread.UncaughtExceptionHandler {

	private static CrashReporter instance;

	private long time;
	private Context context;
	private String versionName;
	private String packageName;
	private String phoneModel;
	private String androidVersion;
	private String board;
	private String brand;
	private String device;
	private String display;
	private String fingerPrint;
	private String host;
	private String id;
	private String model;
	private String product;
	private String tags;
	private String type;
	private String user;
	private HashMap<String, String> customParameters = new HashMap<String, String>();
	private Thread.UncaughtExceptionHandler previousHandler;

	public void addCustomData(String key, String value) {
		customParameters.put(key, value);
	}

	private String createCustomInfoString() {
		String customInfo = "";
		Iterator<String> iterator = customParameters.keySet().iterator();
		while (iterator.hasNext()) {
			String currentKey = (String) iterator.next();
			String currentVal = (String) customParameters.get(currentKey);
			customInfo += currentKey + " = " + currentVal + "\n";
		}
		return customInfo;
	}

	public static CrashReporter getInstance() {
		if (instance == null)
			instance = new CrashReporter();
		return instance;
	}

	public void init(Context context) {
		previousHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
		this.context = context;
	}

	private long getAvailableInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	private long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	private void recoltInformations(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi;
			// version
			pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;
			// package name
			packageName = pi.packageName;
			// Device model
			phoneModel = android.os.Build.MODEL;
			// Android version
			androidVersion = android.os.Build.VERSION.RELEASE;

			board = android.os.Build.BOARD;
			brand = android.os.Build.BRAND;
			device = android.os.Build.DEVICE;
			display = android.os.Build.DISPLAY;
			fingerPrint = android.os.Build.FINGERPRINT;
			host = android.os.Build.HOST;
			id = android.os.Build.ID;
			model = android.os.Build.MODEL;
			product = android.os.Build.PRODUCT;
			tags = android.os.Build.TAGS;
			time = android.os.Build.TIME;
			type = android.os.Build.TYPE;
			user = android.os.Build.USER;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String createInformationString() {
		recoltInformations(context);

		String info = "";
		info += "Version : " + versionName;
		info += "\n";
		info += "Package : " + packageName;
		info += "\n";
		info += "Phone Model" + phoneModel;
		info += "\n";
		info += "Android Version : " + androidVersion;
		info += "\n";
		info += "Board : " + board;
		info += "\n";
		info += "Brand : " + brand;
		info += "\n";
		info += "Device : " + device;
		info += "\n";
		info += "Display : " + display;
		info += "\n";
		info += "Finger Print : " + fingerPrint;
		info += "\n";
		info += "Host : " + host;
		info += "\n";
		info += "ID : " + id;
		info += "\n";
		info += "Model : " + model;
		info += "\n";
		info += "Product : " + product;
		info += "\n";
		info += "Tags : " + tags;
		info += "\n";
		info += "Time : " + time;
		info += "\n";
		info += "Type : " + type;
		info += "\n";
		info += "User : " + user;
		info += "\n";
		info += "Total Internal memory : " + getTotalInternalMemorySize();
		info += "\n";
		info += "Available Internal memory : " + getAvailableInternalMemorySize();
		info += "\n";

		return info;
	}

	public void uncaughtException(Thread t, Throwable e) {
		String report = "";
		Date curDate = new Date();
		report += "Error Report collected on : " + curDate.toString();
		report += "\n";
		report += "\n";
		report += "Informations :";
		report += "\n";
		report += "==============";
		report += "\n";
		report += "\n";
		report += createInformationString();

		report += "Custom Informations :\n";
		report += "=====================\n";
		report += createCustomInfoString();

		report += "\n\n";
		report += "Stack : \n";
		report += "======= \n";
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		report += stacktrace;

		report += "\n";
		report += "Cause : \n";
		report += "======= \n";

		// if the exception was thrown in a background thread inside
		// asyncTask, then the actual exception can be found with getCause
		Throwable cause = e.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			report += result.toString();
			cause = cause.getCause();
		}
		printWriter.close();
		report += "****  End of current Report ***";
		saveAsFile(report);
		previousHandler.uncaughtException(t, e);
	}

	private void saveAsFile(String errorContent) {
		try {
			Random generator = new Random();
			int random = generator.nextInt(99999);
			String fileName = "stack-" + random + ".stacktrace";
			FileOutputStream trace = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			trace.write(errorContent.getBytes());
			trace.close();
		} catch (Exception e) {
		}
	}

	private String[] getErrorFileList(Context context) {
		File dir = context.getFilesDir();
		// try to create the files folder if it doesn't exist
		dir.mkdir();
		// filter for ".stacktrace" files
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".stacktrace");
			}
		};
		return dir.list(filter);
	}

	private boolean isThereAnyErrorFile(Context context) {
		return getErrorFileList(context).length > 0;
	}

	public void checkErrorAndSend(Context context) {
		try {
			if (isThereAnyErrorFile(context)) {
				String wholeErrorText = "";
				String dir = context.getFilesDir().getAbsolutePath();
				String[] errorFileList = getErrorFileList(context);
				int curIndex = 0;
				final int MaxSendMail = 5;
				for (String curString : errorFileList) {
					if (curIndex++ <= MaxSendMail) {
						wholeErrorText += "New Trace collected :\n";
						wholeErrorText += "=====================\n ";
						String filePath = dir + "/" + curString;
						BufferedReader input = new BufferedReader(new FileReader(filePath));
						String line;
						while ((line = input.readLine()) != null) {
							wholeErrorText += line + "\n";
						}
						input.close();
					}

					// delete files
					File curFile = new File(dir + "/" + curString);
					curFile.delete();
				}
				sendError(context, wholeErrorText);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean sendError(Context context, String ErrorContent) {
		Common.log("CrashReporter: sending crash report...");
		try {
			HttpPost post = new HttpPost("http://androidchess.appspot.com/feedback");
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 60000);
			HttpConnectionParams.setSoTimeout(params, 60000);

			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", "crashreport@chesswalk.com"));
			nameValuePairs.add(new BasicNameValuePair("text", ErrorContent));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			httpClient.execute(post);
		} catch (IOException e) {
			Common.log("  CrashReporter: some error happened");
			return false;
		}

		Common.log("  CrashReporter: done");
		return true;
	}
}
