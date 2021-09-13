import java.util.Scanner;
// Monitor que implementa a logica do padrao leitores/escritores
class LE {
  private int leit, escr, leitoreescritor;
  public int x = 0;

  // Construtor
  LE() { 
     this.leit = 0; //leitores lendo (0 ou mais)
     this.escr = 0; //escritor escrevendo (0 ou 1)
     this.leitoreescritor = 0; //leitores lendo (0 ou mais)
  } 
  
  // Entrada para leitores
  public synchronized void EntraLeitor (int id) {
    try { 
      //while (this.escr > 0) {
      if ((this.escr > 0) || (this.leitoreescritor > 0)) {
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.leit++;  //registra que ha mais um leitor lendo
    } catch (InterruptedException e) { }
  }
  
  // Saida para leitores
  public synchronized void SaiLeitor (int id) {
     this.leit--; //registra que um leitor saiu
     if (this.leit == 0) 
           this.notify(); //libera escritor (caso exista escritor bloqueado)
  }
  
  // Entrada para escritores
  public synchronized void EntraEscritor (int id) {
    try { 
      //while ((this.leit > 0) || (this.escr > 0)) {
      if ((this.leit > 0) || (this.escr > 0) || (this.leitoreescritor > 0)) {
         wait();  //bloqueia pela condicao logica da aplicacao 
      }
      this.escr++; //registra que ha um escritor escrevendo
    } catch (InterruptedException e) { }
  }
  
  // Saida para escritores
  public synchronized void SaiEscritor (int id) {
     this.escr--; //registra que o escritor saiu
     notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
  }

  // Entrada para leitura/escrita
  public synchronized void EntraLeituraEscrita (int id) {
    try {
      if ((this.leit > 0) || (this.escr > 0) || (this.leitoreescritor > 0)) {
        wait();  //bloqueia pela condicao logica da aplicacao
      }
      this.leitoreescritor++; //registra que ha um leitor-escritor lendo e escrevendo
    } catch (InterruptedException e) { }
  }

  // Saida para leitura/escrita
  public synchronized void SaiLeituraEscrita (int id) {
    this.leitoreescritor--; //registra que o leitor-escritor saiu
    notifyAll(); //libera leitores e escritores (caso existam leitores ou escritores bloqueados)
  }

}



//Aplicacao de exemplo--------------------------------------------------------
// Leitor
class Leitor extends Thread {
  int id; //identificador da thread
  int delay; //atraso bobo
  int n;
  LE monitor;//objeto monitor para coordenar a lógica de execução das threads


  // Construtor
  Leitor (int id, int delayTime, LE m, int N) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
    this.n = N;
  }

  // Método executado pela thread
  public synchronized void run () {
    try {
      for (int i=0;i<this.n;i++) {
        this.monitor.EntraLeitor(this.id);


        //ver se é primo
        boolean flag = false;
        for (int j = 2; j <= (this.monitor.x / 2); ++j) {
          if (this.monitor.x % j == 0) { //condição para o numero nao ser primo
            flag = true;
            break;
          }
        }
        if (!flag && (this.monitor.x >= 2))
          System.out.println(this.monitor.x + ": primo");
        else
          System.out.println(this.monitor.x + ": nao primo");


        this.monitor.SaiLeitor(this.id);
        sleep(this.delay); 
      }
    } catch (InterruptedException e) { return; }
  }
}

//--------------------------------------------------------
// Escritor
class Escritor extends Thread {
  int id; //identificador da thread
  int delay; //atraso bobo...
  int n;
  LE monitor; //objeto monitor para coordenar a lógica de execução das threads

  // Construtor
  Escritor (int id, int delayTime, LE m, int N) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
    this.n = N;
  }

  // Método executado pela thread
  public synchronized void run () {
    try {
      for (int i=0;i<this.n;i++) {
        this.monitor.EntraEscritor(this.id);

        //escrever id da variável
        this.monitor.x = this.id;

        this.monitor.SaiEscritor(this.id); 
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}
//------------------------------------------------------------------------------------------------------------
// LeituraEscrita
class LeituraEscrita extends Thread {
  int id; //identificador da thread
  int delay; //atraso bobo
  int n;
  LE monitor;//objeto monitor para coordenar a lógica de execução das threads

  // Construtor
  LeituraEscrita (int id, int delayTime, LE m, int N) {
    this.id = id;
    this.delay = delayTime;
    this.monitor = m;
    this.n = N;
  }

  // Método executado pela thread
  public synchronized void run () {
    try {
      for (int i=0;i<this.n;i++) {
        this.monitor.EntraLeituraEscrita(this.id);

        if(this.monitor.x % 2 != 0){
          System.out.println(this.monitor.x + ": impar");
          this.monitor.x = (this.monitor.x)*2;}

        else{
          System.out.println(this.monitor.x + ": par");
          this.monitor.x = (this.monitor.x)*2;}

        this.monitor.SaiLeituraEscrita(this.id);
        sleep(this.delay);
      }
    } catch (InterruptedException e) { return; }
  }
}

//--------------------------------------------------------
// Classe principal
class LeituraeEscrita {

  public static void main (String[] args) {
    Scanner sc = new Scanner(System.in);
    System.out.println("Entre com o numero de threads L");
    int L = sc.nextInt();
    System.out.println("Entre com o numero de threads E");
    int E = sc.nextInt();
    System.out.println("Entre com o numero de threads LE");
    int LE = sc.nextInt();
    System.out.println("Entre com o numero de interacoes");
    int N = sc.nextInt(); //Numero de interações

    sc.close();

    int i;
    LE monitor = new LE();                  // Monitor (objeto compartilhado entre leitores e escritores)
    Leitor[] l = new Leitor[L];             // Threads leitores
    Escritor[] e = new Escritor[E];         // Threads escritores
    LeituraEscrita[] le = new LeituraEscrita[LE]; // Threads leitor-escritores

    //inicia o log de saida
    
    for (i=0; i<L; i++) {
       l[i] = new Leitor(i+1, (i+1)*500, monitor, N);
       l[i].start(); 
    }
    for (i=0; i<E; i++) {
       e[i] = new Escritor(i+1, (i+1)*500, monitor, N);
       e[i].start();
    }
    for (i=0; i<LE; i++) {
       le[i] = new LeituraEscrita(i+1, (i+1)*500, monitor, N);
       le[i].start();
    }
  }
}
