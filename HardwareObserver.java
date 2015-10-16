package ca.ucalgary.seng301.myvendingmachine;

import ca.ucalgary.seng301.vendingmachine.Coin;
import ca.ucalgary.seng301.vendingmachine.hardware.*;

public class HardwareObserver 
	implements SelectionButtonListener, CoinReceptacleListener{
	private int totalCredit;
	
	public HardwareObserver(VendingMachine vm){
		setTotal(0);
		vm.getCoinReceptacle().register(this);
		for (int i = 0; i < vm.getNumberOfSelectionButtons(); i++){
			vm.getSelectionButton(i).register(this);
		}
	}
	
	public int getTotal() {return totalCredit;}
	public void setTotal(int total) {this.totalCredit = total;}
	
	// All
	@Override
	public void enabled(AbstractHardware<AbstractHardwareListener> hardware) {
		hardware.enable();
	}
	@Override
	public void disabled(AbstractHardware<AbstractHardwareListener> hardware) {
		hardware.disable();
	}

	// CoinReceptacleListener
	@Override
	public void coinAdded(CoinReceptacle receptacle, Coin coin) {
		try {
			receptacle.storeCoins();
			totalCredit += coin.getValue();
		} catch (CapacityExceededException | DisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void coinsRemoved(CoinReceptacle receptacle) {}
	@Override
	public void coinsFull(CoinReceptacle receptacle) {
		try {
			receptacle.returnCoins();
		} catch (CapacityExceededException | DisabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	// SelectionButtonListener
	@Override
	public void pressed(SelectionButton button) {
		
	}



}
