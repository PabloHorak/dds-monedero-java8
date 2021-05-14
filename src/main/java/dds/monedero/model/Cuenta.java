package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos =  new ArrayList <>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double cuanto) {
    validacionesGenericasSobreDeposito(cuanto);
    Movimiento unMovimiento = new Movimiento(LocalDate.now(), cuanto, true);
    movimientos.add (unMovimiento);
    setSaldo(cuanto);
  }
  public void validacionesGenericasSobreDeposito (double unMonto){

    validacionValorPositivo(unMonto);
    validacionCantidadDepositosDiarios();

  }

  public void validacionCantidadDepositosDiarios(){
    if (getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count() >= 3) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + 3 + " depositos diarios");
    }
  }

  public void sacar(double cuanto) {
    validacionesGenericasSobreExtraccion(cuanto);
    Movimiento unMovimientoExtraccion= new Movimiento(LocalDate.now(), cuanto, false);
    movimientos.add(unMovimientoExtraccion);
  }
  public void validacionesGenericasSobreExtraccion (double unMonto){

    validacionValorPositivo(unMonto);
    validacionMontoSuperiorSaldo(unMonto);
    validacionMontoDeExtracionesDiarias(unMonto);
  }
  public void validacionValorPositivo (double unMonto){
    if (unMonto <= 0) {
      throw new MontoNegativoException(unMonto + ": el monto a ingresar debe ser un valor positivo");
    }
  }
  public void  validacionMontoDeExtracionesDiarias(double unMonto){
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limite = 1000 - montoExtraidoHoy;
    if (unMonto > limite) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + 1000
          + " diarios, límite: " + limite);
    }
  }
  public void validacionMontoSuperiorSaldo(double unMonto){
    if (getSaldo() - unMonto < 0) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
  }



  public void agregarMovimiento(LocalDate fecha, double cuanto, boolean esDeposito) {
    Movimiento movimiento = new Movimiento(fecha, cuanto, esDeposito);
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> !movimiento.isDeposito() && movimiento.getFecha().equals(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo += saldo;
  }



}
