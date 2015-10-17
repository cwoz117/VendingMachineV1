package ca.ucalgary.seng301.myvendingmachine;

import ca.ucalgary.seng301.vendingmachine.Coin;
import ca.ucalgary.seng301.vendingmachine.hardware.*;

public class HardwareObserver 
	implements SelectionButtonListener, CoinReceptacleListener{
	
	private int totalCredit;
	private VendingMachine vm;
	
	public HardwareObserver(VendingMachine vm){
		setTotal(0);
		this.vm = vm;
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
			e.printStackTrace();
		}	
	}
	
	// SelectionButtonListener
	@Override
	public void pressed(SelectionButton button) {
		int index = findLocation(button);
		if (totalCredit >= vm.getPopKindCost(index)){
			// store coins
			try {
				vm.getCoinReceptacle().storeCoins();
				vm.getPopCanRack(index).dispensePop();
				returnChange(index);
			} catch (CapacityExceededException | DisabledException | EmptyException e) {
				e.printStackTrace();
			}
		}
	}
	private void returnChange(int priceIndex){
		int change = totalCredit - vm.getPopKindCost(priceIndex);
		int i = vm.getNumberOfCoinRacks() - 1;
		while ((change > 0) && (i > 0)) {
			if (change >= vm.getCoinKindForRack(i)){
				try {
					vm.getCoinRack(i).releaseCoin();
					change -= vm.getCoinKindForRack(i);
				} catch (CapacityExceededException | EmptyException | DisabledException e) {
					e.printStackTrace();
				}
			} else {
				i --;
			}
		}
	}
	private int findLocation(SelectionButton button){
		for (int i = 0; i < vm.getNumberOfSelectionButtons(); i++){
			if (button == vm.getSelectionButton(i)){
				return i;
			}
		}
		return -1;
	}

}
