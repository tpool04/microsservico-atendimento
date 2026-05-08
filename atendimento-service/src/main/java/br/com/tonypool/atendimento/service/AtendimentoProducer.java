package br.com.tonypool.atendimento.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import br.com.tonypool.atendimento.kafka.AtendimentoEvent;

@Service
public class AtendimentoProducer {

    @Autowired
    @Qualifier("clienteRequestKafkaTemplate")
    // use raw type to match the existing bean regardless of its generic parameter
    private KafkaTemplate kafkaTemplate;

    public void enviarEventoAtendimento(AtendimentoEvent event) {
        kafkaTemplate.send("atendimentos.finalizados", event);
    }
}
