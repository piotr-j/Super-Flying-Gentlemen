/*
 * Super Flying Gentlemen
 * Copyright (C) 2014  Piotr Jastrzębski <me@piotrjastrzebski.io>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.piotrjastrzebski.sfg.events;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;

public class EventLoop {
	private Array<Event> eventQueueCurrent;
	private Array<Event> eventQueueNext;
	private EventPool eventPool;
	private IntMap<Array<EventListener>> listenerMap;
	
	public EventLoop(){
		listenerMap = new IntMap<Array<EventListener>>();
        eventQueueCurrent = new Array<Event>();
        eventQueueNext = new Array<Event>();
		eventPool = new EventPool();
	}

    /**
     * Swaps queues so events can be added during event processing
     * This makes it possible to create an infinite loops of events
     */
    private void swapQueues(){
        final Array<Event> tempEventArray = eventQueueCurrent;
        eventQueueCurrent = eventQueueNext;
        eventQueueNext = tempEventArray;
    }
	
	/**
	 * Register listener for given type, if not registered
	 */
	public void register(EventListener listener, int type){
		if (!listenerMap.containsKey(type)){
			listenerMap.put(type, new Array<EventListener>());
		}
		final Array<EventListener> listeners = listenerMap.get(type);
		if (!listeners.contains(listener, true)){
			listeners.add(listener);
		}
	}
	
	/**
	 * Unregisters listener from given event type.
	 * 
	 */
	public void unregister(EventListener listener, int type){
		if (listenerMap.containsKey(type)){
			listenerMap.get(type).removeValue(listener, true);
		}
	}
	
	/**
	 * Dispatch events. Events are processed in order they ware queued.
	 * Queuing events in handler may cause feedback loops.
	 */
	public void update(){
        // swap queues so any events created will go to next one
        swapQueues();
        for (int i = 0; i < eventQueueCurrent.size; i++) {
            final Event e = eventQueueCurrent.get(i);
            final Array<EventListener> listeners = listenerMap.get(e.getType());
            for (int j = 0; j < listeners.size; j++) {
                listeners.get(j).handleEvent(e);
            }
            eventPool.free(e);
        }
        eventQueueCurrent.clear();
	}
	
	/**
	 * Create event and add to the queue
	 */
	public void queueEvent(int type){
		queueEvent(type, null);
	}
	
	/**
	 * Create event and add to the queue
	 */
	public void queueEvent(int type, Object data){
		queueEvent(type, data, null);
	}
	
	/**
	 * Create event and add to the queue
	 */
	public void queueEvent(int type, Object data, Object source){
		// no one listens for this event, discard it
		if (!listenerMap.containsKey(type))
			return;
		if (listenerMap.get(type).size == 0)
			return;
		final Event e = getEvent();
		e.init(type, data, source);
        eventQueueCurrent.add(e);
	}
	
	/**
	 * Add event to the queue
	 */
	public void queueEvent(Event e){
		int type = e.getType();
		// no one listens for this event, discard it
		if (!listenerMap.containsKey(type))
			return;
		if (listenerMap.get(type).size == 0)
			return;

        eventQueueCurrent.add(e);
	}
	
	/**
	 * Returns empty event from the event pool
	 * Events are freed by EventLoop itself
	 */
	public Event getEvent(){
		return eventPool.obtain();
	}

    /**
     * Clear all pending events
     */
    public void clear() {
        for (int i = 0; i < eventQueueCurrent.size; i++) {
            eventPool.free(eventQueueCurrent.get(i));
        }
        eventQueueCurrent.clear();
        for (int i = 0; i < eventQueueNext.size; i++) {
            eventPool.free(eventQueueNext.get(i));
        }
        eventQueueNext.clear();
        eventPool.clear();
    }
}
