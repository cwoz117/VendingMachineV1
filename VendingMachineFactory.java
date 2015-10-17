package ca.ucalgary.seng301.myvendingmachine;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;


import ca.ucalgary.seng301.vendingmachine.*;
import ca.ucalgary.seng301.vendingmachine.hardware.*;
import ca.ucalgary.seng301.vendingmachine.parser.*;

public class VendingMachineFactory implements IVendingMachineFactory{

	private VendingMachine vm;
	@SuppressWarnings("unused")		// This object is referenced FROM observer, thus is being used.
	private HardwareObserver observer;
	
	public static void main(String[] args) throws ParseException, FileNotFoundException {
		int count = 0;
		// TODO Modify this initializer to run your own good scripts
		String[] goodScripts = { "good-script"};

		for (String script : goodScripts) {
			try {
				count++;
				new VendingMachineFactory(script);
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println();
			}
		}

		// TODO Modify this initializer to run your own bad scripts
		String[] badScripts = { "bad-script1", "bad-script2" };
		for (String script : badScripts) {
			try {
				count++;
				new VendingMachineFactory(script);
			} catch (Throwable t) {
				t.printStackTrace();
				System.err.println();
			}
		}
		System.err.println(count + " scripts executed");
	}
	public VendingMachineFactory(String path) throws ParseException, 
											  FileNotFoundException, DisabledException {
		Parser p = new Parser(new FileReader(path));
		p.register(this);
		p.setDebug(true);
		p.process(path);
	}

	@Override
	public List<Object> extract() {
		Object[] product = vm.getDeliveryChute().removeItems();
		List<Object> output= new ArrayList<Object>();
		for (int i = 0; i < product.length; i++){
			output.add(product[i]);
		}
		return output;
	}
	@Override
	public void insert(int value) throws DisabledException {
		if (value <= 0){
			throw new IllegalArgumentException("Cannont insert a non-positive coin");
		}
		vm.getCoinSlot().addCoin(new Coin(value));
	}
	@Override
	public void press(int value) {
		if ((value < 0) || (value >= vm.getNumberOfSelectionButtons())){
			throw new IllegalArgumentException("Pressed an invalid button");
		}
		vm.getSelectionButton(value).press();
	}
	@Override
	public void construct(List<Integer> coinKinds, int selectionButtonCount, int coinRackCapacity,
			int popCanRackCapacity, int receptacleCapacity) {		
		
		int[] coinLocation = new int[coinKinds.size()];
		for (int i = 0; i< coinLocation.length; i++){
			coinLocation[i] = coinKinds.get(i);
		}
		vm = new VendingMachine(coinLocation, selectionButtonCount, coinRackCapacity, 
												popCanRackCapacity, receptacleCapacity);
		observer = new HardwareObserver(vm);
		
	}
	@Override
	public void configure(List<String> popNames, List<Integer> popCosts) {
		vm.configure(popNames, popCosts);
	}
	@Override
	public void load(List<Integer> coinCounts, List<Integer> popCanCounts) {
		int coinMax = vm.getNumberOfCoinRacks();
		int popMax = vm.getNumberOfPopCanRacks();
		if (coinCounts.size() != coinMax) {
			throw new IllegalArgumentException("The number of coins does not match the VM");
		}
		if (popCanCounts.size() != popMax){
			throw new IllegalArgumentException("The number of pops does not match the VM");
		}
		for (int i = 0; i < coinCounts.size(); i++){
			for (int j = 0; j < coinCounts.get(i).intValue(); j ++){
				vm.getCoinRack(i).loadWithoutEvents(new Coin(vm.getCoinKindForRack(i)));
			}
		}
		for (int i = 0; i < popCanCounts.size(); i++){
			for (int j = 0; j < popCanCounts.get(i).intValue(); j ++){
				vm.getPopCanRack(i).loadWithoutEvents(new PopCan(vm.getPopKindName(i)));
			}
		}

	}
	@Override
	public VendingMachineStoredContents unload() {
		VendingMachineStoredContents output = new VendingMachineStoredContents();
		for (int i = 0; i < vm.getNumberOfCoinRacks(); i++){
			output.unusedCoinsForChange.add(vm.getCoinRack(i).unloadWithoutEvents());
		}
		
		output.paymentCoinsInStorageBin.addAll(vm.getStorageBin().unloadWithoutEvents());
		
		for (int i = 0; i < vm.getNumberOfPopCanRacks(); i++){
			output.unsoldPopCans.add(vm.getPopCanRack(i).unloadWithoutEvents());
		}		
		return output;
	}
}
