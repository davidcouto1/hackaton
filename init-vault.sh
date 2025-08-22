#!/bin/sh
set -e

# Aguarda o Vault subir
until curl -s http://vault:8200/v1/sys/health > /dev/null; do
  echo "Aguardando o Vault iniciar..."
  sleep 2
done

export VAULT_ADDR='http://vault:8200'
vault login $VAULT_DEV_ROOT_TOKEN_ID > /dev/null 2>&1

# Insere segredos do banco de dados
vault kv put secret/hackaton spring.datasource.url='jdbc:sqlserver://sqlserver:1433;databaseName=hack' spring.datasource.username='hack' spring.datasource.password='Password23'

# Insere segredos do EventHub
vault kv put secret/hackaton eventhub.connection-string='Endpoint=sb://eventhack.servicebus.windows.net/;SharedAccessKeyName=hack;SharedAccessKey=HeHeVaVqyVkntO2FnjQcs2Ilh/4MUDo4y+AEhKp8z+g=;EntityPath=simulacoes' eventhub.name='simulacoes'

echo "Segredos inseridos no Vault com sucesso."
