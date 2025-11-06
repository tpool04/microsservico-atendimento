package br.com.tonypool.atendimento.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import br.com.tonypool.atendimento.dto.ClienteDTO;

@Component
public class ClienteCache {

    private final Map<String, ClienteDTO> cache = new ConcurrentHashMap<>();

    public void salvar(String correlationId, ClienteDTO cliente) {
        cache.put(correlationId, cliente);
    }

    public ClienteDTO buscar(String correlationId) {
        return cache.get(correlationId);
    }

    public void remover(String correlationId) {
        cache.remove(correlationId);
    }
}
