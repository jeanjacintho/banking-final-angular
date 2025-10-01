# Guia de Contribuição

Obrigado por considerar contribuir com o Banking System! Este documento fornece diretrizes para contribuir com o projeto.

## 📋 Como Contribuir

### 1. Fork do Projeto
1. Clone o projeto
2. Crie uma branch para sua feature

### 2. Desenvolvimento
```bash
# Clone o repositório
git clone https://github.com/jeanjacintho/banking-final-angular.git
cd banking-final-angular

# Crie uma branch
git checkout -b feature/nova-funcionalidade

# Instale dependências
cd web && npm install
cd ../api && ./mvnw dependency:resolve
```

### 3. Padrões de Código

#### Backend (Java/Spring Boot)
- Use Java 17+
- Siga as convenções do Spring Boot
- Documente métodos públicos
- Escreva testes unitários

#### Frontend (Angular/TypeScript)
- Use Angular 20+
- Siga o Angular Style Guide
- Use TypeScript strict mode
- Escreva testes unitários

### 4. Commits
Use mensagens de commit descritivas:
```bash
git commit -m "feat: adiciona autenticação JWT"
git commit -m "fix: corrige validação de formulário"
git commit -m "docs: atualiza documentação da API"
```

### 5. Pull Request
1. Certifique-se de que os testes passam
2. Atualize a documentação se necessário
3. Crie um Pull Request descritivo
4. Aguarde a revisão

## 🧪 Testes

### Backend
```bash
cd api
./mvnw test
```

### Frontend
```bash
cd web
npm test
```

### Testes E2E
```bash
cd web
npm run e2e
```

## 📝 Relatório de Bugs

Ao reportar bugs, inclua:
- Descrição detalhada
- Passos para reproduzir
- Comportamento esperado vs atual
- Screenshots (se aplicável)
- Informações do ambiente

## 💡 Sugestões de Melhorias

Para sugerir melhorias:
- Descreva o problema ou necessidade
- Explique a solução proposta
- Considere alternativas
- Discuta impactos

## 🔧 Configuração do Ambiente

### Desenvolvimento Local
```bash
# Backend
cd api
./mvnw spring-boot:run

# Frontend
cd web
npm start

# Banco de dados
docker run -d -p 5432:5432 -e POSTGRES_DB=banking postgres:16
```

### Docker
```bash
cd infra/compose
docker-compose up -d
```

