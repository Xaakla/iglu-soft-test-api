# Documentação do Projeto

## Introdução
Este documento descreve o projeto Iglu Soft Test, desenvolvido para o processo seletivo da Iglu Soft. Este backend
foi construído para ser a api para um sistema de venda de lanches, de modo que alguns lanches são opções de cardápio 
e outros podem conter ingredientes personalizados.

### Como rodar essa API na sua maquina?
#### Pré requisitos: 
- Ter o docker instalado, para conseguir criar o container do app.
- Ter o git instalado, para ter acesso ao comando `git clone` no terminal.

Clone o repositório usando o comando: `git clone https://github.com/Xaakla/iglu-soft-test-api.git`.
Ou se preferir baixe o zip do projeto deste mesmo link.

Com o Docker aberto, abra o terminal no diretório do projeto e execute o comando `./mvnw spring-boot:build-image` para criar a imagem da aplicação.

Para criar um container e inicializar a aplicação, execute o comando `docker compose up`.

Feito isso a api já deve estar rodando na porta 8080 da sua máquina.

### Objetivos da API
- Ter endpoints para *Listar*, *Criar*, *Atualizar* e *Deletar* os INGREDIENTES, LANCHES e PROMOÇÕES no sistema.
- Ter um endpoint para fazer os pedidos dos lanches cadastrados no sistema e visualizar os preços já com descontos, se tiver alguma promoção ativa.

## Design e Implementação

### Sistema de preços

Para garantir precisão e consistência nos cálculos financeiros, a API representa preços em centavos.
Isso evita problemas de arredondamento comuns com valores decimais, facilita operações matemáticas,
e melhora a compatibilidade com sistemas financeiros que esperam valores inteiros. Além disso, o uso de inteiros
para representar preços é mais eficiente em termos de desempenho.

### Estrutura do Código

Feito na estrutura padrão MVC (Model-View-Controller), a API tem uma pasta "controllers", onde se encontra os endpoints da aplicação.
A pasta "services" agrupa os serviços do sistema, contendo toda a lógica e regras de negócio. Dentro da pasta "database",
está agrupa mais duas pastas, "entities" e "repositories", que são responsáveis por agrupar as entidades do sistema e
os repositórios respectivamente.

### Principais Classes
- **Ingredient**: Entidade usada para representar os ingredientes no banco de dados
- **Dish**: Entidade usada para representar os lanches no banco de dados
- **DishIngredientQuantity**: Entidade usada para representar a relação entre o ingrediente e o lanche no banco de dados
- **Offer**: Entidade usada para representar as ofertas no banco de dados
- **OfferIngredientMinQuantity**: Entidade usada para representar a relação entre o ingrediente e a oferta no banco de dados

### Principais Funcionalidades
- **Pedidos**: Essa funcionalidade permite escolher lanches cadastrados e visualizar seus preços com desconto, caso exista alguma promoção ativa.
- **Ofertas**: O sistema abrange dois tipos de oferta, desconto em porcentagem no preço total do pedido e o compre X e pague Y.

Além do CRUD de ingredientes e lanches que são funcionalidades básicas do sistema.
