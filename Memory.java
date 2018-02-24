
import java.io.File;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;

/**
 *
 * @author Jay Speights
 */
public class Memory {
  private static int timeout;
  private static final int[] memory = new int[2000];

  private static int clock;

  private static void load(String filename) throws Exception {
    Scanner scanner = new Scanner(new File(filename));
    int index = 0;
    while (scanner.hasNext()) {
      String token = scanner.nextLine();
      int i = 0;
      String num = "";
      if (token.length() == 0) continue;
      if (token.charAt(0) == '.') {
        token = token.substring(1);
        while (i < token.length() && Character.isDigit(token.charAt(i))) {
          num += token.charAt(i);
          i++;
        }
        if (num.length() > 0) {
          index = Integer.parseInt(num);
        }
      } else {
        while (i < token.length() && Character.isDigit(token.charAt(i))) {
          num += token.charAt(i);
          i++;
        }
        if (num.length() > 0) {
          memory[index] = Integer.parseInt(num);
          index++;
        }
      }
    }
  }

  public static void main(String... args) {
    timeout = Integer.parseInt(args[1]);
    clock = 0;

    try {
      load(args[0]);
      System.out.println("DONE LOADING");
    } catch (Exception e) {
      System.out.println(e);
      return;
    }


    Scanner input = new Scanner(System.in);

    String in;
    String[] req;
    int addr;

    try{
      BufferedWriter log = new BufferedWriter(new FileWriter("debug.log", true));
      while (true) {
        in = input.nextLine();
        log.append(in+"\n");
        log.flush();
        req=in.split(" ");
        switch (req[0]) {
          case "READ":
            clock++;
            if (clock == timeout) {
              clock = 0;
              System.out.println("TIMER");
            }
            addr = Integer.parseInt(req[1]);
            log.append("\t"+addr+"->"+memory[addr]+"\n");
            log.flush();
            System.out.println(memory[addr]);
            log.append("\t"+memory[addr]+"\n");
            log.flush();
            break;
          case "WRITE":
            addr = Integer.parseInt(req[1]);
            memory[addr] = Integer.parseInt(req[2]);
            break;
          case "END":
            return;
        }
      }
    } catch (Exception e) {}


  }
}
