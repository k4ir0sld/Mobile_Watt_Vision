# Watt Vision — Contexto Mobile

## Tecnologias
- Kotlin
- Android Studio
- API REST
- JSON

## Objetivo
Consultar e visualizar medições energéticas e cadastros.

## Integração
- Back-end: Node.js + Express
- Banco: MySQL 8
- Comunicação IoT: MQTT
- Endpoints: documentados na coleção do Postman

## Funcionalidades
- [ ] Login
- [ ] Consulta de locais
- [ ] Consulta de setores
- [ ] Consulta de equipamentos
- [ ] Visualização de medições
- [ ] Filtros do protótipo
- [ ] Atualização periódica

## Decisões confirmadas
- O Android consome a API do back-end.
- O Android não acessa diretamente o MySQL.
- Registros inativados não devem ser excluídos fisicamente.

## Pendências
- Confirmar o contrato de autenticação.
- Mapear os endpoints necessários para cada tela.
- Confirmar o intervalo de atualização das medições.
- Confirmar os endpoints de filtros e dashboard.

## Estado atual
Atualizar conforme o desenvolvimento.

## Próxima tarefa
Definir após o diagnóstico do projeto.