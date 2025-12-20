package com.resinex.resinex.Services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class AlertStreamService {

    //list of the frontend subscribers to this service
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    //method for subscribing in the service
    public SseEmitter subscribe() {
        //create a Sse Emitter with timeout=0
        SseEmitter emitter = new SseEmitter(0L);
        //add it to the list of subscribers
        emitters.add(emitter);

        //cleanup handlers to prevent memory leaks
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));
        //return the subscriber
        return emitter;
    }

    //method for sending the event across all subscribers
    public void send(String eventName, Object data) {
        for (SseEmitter emitter : emitters) {
            //send name of the event and the data needed for it
            try {
                emitter.send(
                        SseEmitter.event()
                                .name(eventName)
                                .data(data)
                );
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }
}
