package net.coderextreme.icbm;
/*
    ICBM (Internet Chat by MUD)

    Copyright (C) 1997, 1998, 2000  John Carlson

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Vector;

public interface MUDRemote extends Remote {
	public Vector<Object> inventory() throws RemoteException;
	public Vector<Object> environment() throws RemoteException;
	public Vector<Object> get_siblings() throws RemoteException;
	public void setName(String a) throws RemoteException;
	public String getName() throws RemoteException;

	public int tell(Vector<Object> message) throws RemoteException;
	public int tell_all_children(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int tell_children(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int tell_coowners(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int tell_siblings(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int tell_all_parents(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int tell_parents(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int tell_everything(Vector<Object> message, Vector<Object> objects_not_to_tell) throws RemoteException;
	public int command(MUDRemote subject, Vector<Object> comm) throws RemoteException;
	public int add(MUDRemote new_location) throws RemoteException;
	public int addobject(MUDRemote new_child) throws RemoteException;
	public int addToInventory(InventoryItem i) throws RemoteException;
	public int addToInventory(Vector<Object> v) throws RemoteException;
	public int remove(MUDRemote from) throws RemoteException;
	public int removeInventoryElement(MUDRemote mr) throws RemoteException;
	public boolean ping() throws RemoteException;
	public String getURL() throws RemoteException;
	public void setURL(String url) throws RemoteException;
	public int processInput(InputLine line) throws RemoteException;
	public void readAliases() throws RemoteException; // I guess that someone can read someone else's aliases.  I don't think this hurts anything, because the function doesn't return anything
	public String id() throws RemoteException;
	public String get(String prop) throws RemoteException;
	public void remove(String prop) throws RemoteException;
	public void put(String prop, String value) throws RemoteException;
	public void putInt(String prop, int value) throws RemoteException;
	public int getInt(String prop) throws RemoteException;
	public Enumeration keys() throws RemoteException;
	public boolean isPropertySet(String index) throws RemoteException;
	public Enumeration getPropertyKeys() throws RemoteException;
	public void putProp(String index) throws RemoteException;
	public void copyFrom(MUDRemote o) throws RemoteException;
	public MUDRemote nextParent(Enumeration e) throws RemoteException;
	public MUDRemote nextItem(Enumeration e) throws RemoteException;
	public boolean contains(MUDRemote o) throws RemoteException;
}
