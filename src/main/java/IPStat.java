import java.io.BufferedReader;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Mac on 15/12/15.
 */
public class IPStat {
    static HashMap<String, Integer> mp = new HashMap<String, Integer>();

    public static void main(String[] args) {
        //please configure the path yourself
        String base="/Users/Mac/Downloads/apache-tomcat-8.0.29/logs/localhost_access_log.";
        IPStat ipstat = new IPStat();
        String cur_time=args[0],final_time=args[1],service=args[2];

        ipstat.TestIP(base + ChangeFormat(cur_time) + ".txt", service);
        while(true) {
            if (cur_time.equals(final_time)) {
                break;
            } else {
                cur_time = next_date(cur_time);
                ipstat.TestIP(base + ChangeFormat(cur_time) + ".txt", service);
            }
        }
        Iterator it = mp.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            System.out.println("IP: " + key + " num: " + mp.get(key) + " type: " + TypeofIP(key));
        }
    }
    /**
     * return the information of IP,num,type.
     * @param file
     * @param InterfaceName
     */
    public void TestIP(String file,String InterfaceName) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String s;
            while ((s = in.readLine()) != null) {
                String[] values = s.split(" ");
                //values[0] is IP,values[7] is service,value[9] is status.
                if (values[9].equals("200") && Hasprefix(values[7],InterfaceName)) {
                    if (mp.containsKey(values[0])){
                        int val=mp.get(values[0]).intValue();
                        val++;
                        mp.put(values[0],val);
                    }
                    else mp.put(values[0], 1);
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * return the type of IP,
     * 1 for 10.*,
     * 2 for 172.*,
     * 3 for others.
     */
    public static int TypeofIP(String IP) {
        if(IP.charAt(0)=='1' && IP.charAt(1)=='0') return 1;
        else if(IP.charAt(0)=='1' && IP.charAt(1)=='7' && IP.charAt(2)=='2') return 2;
        else return 3;
    }
    /**
     * calculate next date
     * @param cur_date
     * @return
     */
    public static String next_date(String cur_date){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
        String ret="";
        try {
            Date date = sdf.parse(cur_date);
            ret= sdf.format(new Date(date.getTime()+24 * 60 * 60 * 1000));
        }catch (Exception ex){
            logInfo("Date transforms wrongly.");
        }
        return ret;
    }

    /**
     * ChangeFormat
     * @param cur_time
     * @return
     */
    public static String ChangeFormat(String cur_time){
        String ns;
        ns=Stringinsert(cur_time,'-',4);
        return Stringinsert(ns,'-',7);
    }

    /**
     * Insert a char in a String
     * @param a
     * @param b
     * @param t
     * @return
     */

    public static String Stringinsert(String a,char b,int t){
        return a.substring(0,t)+b+a.substring(t,a.length());
    }

    /**
     * test if s has prefix pre
     * @param s
     * @param pre
     * @return
     */
    public static boolean Hasprefix(String s,String pre){
        int len=pre.length(),slen=s.length();
        if(slen<len) return false;
        for(int i=0;i<len;i++){
            if(s.charAt(i)!=pre.charAt(i)) return false;
        }
        return true;
    }

    /**
     * loginfo
     * @param message
     */
    public static void logInfo(String message){
        if(message.trim().length() <= 0){
            System.out.println("");
        }else {
            System.out.println("******INFO::[" + Thread.currentThread().getId() + "@" + Thread.currentThread().getName() + " :: " + message);
        }
    }
}
