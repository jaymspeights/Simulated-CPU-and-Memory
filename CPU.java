
import java.util.Scanner;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author Jay Speights
 */
public class CPU {

  static Scanner input;
  static PrintWriter output;

  //Current mode of execution
  //can be 0=USER, 1=KERNAL, 2=TIMER (timer interrupt called), 3=INT (software interrupt called)
  int mode = 0;

  //registers
  int sp = 999;
  int pc = 0;
  int ir = -1;
  int ac = 0;
  int x = 0;
  int y = 0;

  public void exec() throws Exception {
    while (true) {
      if (mode == 3 || mode == 2) {
        int new_pc = mode==2? 1000 : 1500;
        mode = 1;
        write(1999, sp);
        write(1998, pc);
        sp = 1998;
        pc = new_pc;
      }
      ir = read(pc++);
      switch (ir) {
        //Load Value
        case 1:
          ac = read(pc++);
          break;

        //Load addr
        case 2:
          ac = read(read(pc++));
          break;

        //LoadInd addr
        case 3:
          ac = read(read(read(pc++)));
          break;

        //LoadIdxX addr
        case 4:
          ac = read(read(pc++) + x);
          break;

        //LoadIdxY
        case 5:
          ac = read(read(pc++) + y);
          break;

        //LoadSpX
        case 6:
          ac = read(sp+x);
          break;

        //Store addr
        case 7:
          write(read(pc++), ac);
          break;

        //Get
        case 8:
          ac = (int)(Math.random()*100+1);
          break;

        //Put port
        case 9:
          int select = read(pc++);
          if (select == 1)
            System.out.print(ac);
          else if (select == 2)
            System.out.print((char)ac);
          break;

        //AddX
        case 10:
          ac += x;
          break;

        //AddY
        case 11:
          ac += y;
          break;

        //SubX
        case 12:
          ac -= x;
          break;

        //SubY
        case 13:
          ac -= y;
          break;

        //CopyToX
        case 14:
          x = ac;
          break;

        //CopyFromX
        case 15:
          ac = x;
          break;

        //CopyToY
        case 16:
          y = ac;
          break;

        //CopyFromY
        case 17:
          ac = y;
          break;

        //CopyToSp
        case 18:
          sp = ac;
          break;

        //CopyFromSp
        case 19:
          ac = sp;
          break;

        //Jump addr
        case 20:
          pc = read(pc);
          break;

        //JumpIfEqual addr
        case 21:
          if (ac == 0)
            pc = read(pc);
          else pc++;
          break;

        //JumpIfNotEqual addr
        case 22:
          if (ac != 0)
            pc = read(pc);
          else pc++;
          break;

        //Call addr
        case 23:
          write(--sp, pc+1);
          pc = read(pc);
          break;

        //Ret
        case 24:
          pc = read(sp);
          write(sp++, 0);
          break;

        //IncX
        case 25:
          x++;
          break;

        //DecX
        case 26:
          x--;
          break;

        //Push
        case 27:
          write(--sp, ac);
          break;

        //Pop
        case 28:
          ac = read(sp);
          write(sp++, 0);
          break;

        //Int
        case 29:
          mode = 3;
          break;

        //IRet
        case 30:
          if (mode == 1) {
            pc = read(1998);
            sp = read(1999);
            mode = 0;
          }
          break;

        //End
        case 50:
          output.println("END");
          output.flush();
          return;

        //Invalid Instruction
        default:
          System.out.println("\nFound invalid instruction " + ir + " at addr " + (pc-1));
          return;
      }
    }
  }

  private int read(int addr) throws Exception {
    if (addr > 999 && mode != 1)
      throw new Exception("Cannot access address " +addr+ " while not in KERNAL mode");

    CPU.output.println("READ " + addr);
    CPU.output.flush();
    String res = CPU.input.nextLine();
    if (res.equals("TIMER")) {
      res = CPU.input.nextLine();
      if (mode == 0)
        mode = 2;
    }
    return Integer.parseInt(res);
  }

  private void write(int addr, int val) throws Exception {
    if (addr > 999 && mode != 1)
      throw new Exception("Cannot access address " +addr+ " while not in KERNAL mode");

    CPU.output.println("WRITE " + addr + " " + val);
    CPU.output.flush();
  }

  public static void main(String... args) {
    try {
      if (args.length < 2) {
        System.out.println("Please specify an input file as the first argument");
        return;
      }
      Runtime rt = Runtime.getRuntime();
      Process mem = rt.exec("java Memory " + args[0] +" "+ args[1]);

      InputStream is = mem.getInputStream();
      OutputStream os = mem.getOutputStream();

      input = new Scanner(is);
      output = new PrintWriter(os);

      wait:
      while(true) {
        String res = input.nextLine();
        switch (res) {
          case "DONE LOADING":
            break wait;
          case "ERROR":
            System.out.println("Error loading file " + args[0]);
            return;
          default:
            System.out.println(res);
        }
      }

      /*****************************************/
      /*          Program Execution            */
      /*****************************************/

      // mode = 0;
      //
      // sp = 999;
      // pc = 0;
      // ir = -1;
      // ac = 0;
      // x = 0;
      // y = 0;

      CPU cpu = new CPU();
      cpu.exec();

    } catch (Exception e) {
        e.printStackTrace();
    }
  }

}
