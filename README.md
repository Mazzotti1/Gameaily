## Sobre

**What's The Game?** é projeto é composto por um aplicativo móvel desenvolvido em Koltin com o com seu backend desenvolvido em Kotlin e com um banco de dados Postgres.

![recursografico](https://github.com/Mazzotti1/WhatsTheGame/assets/70278577/8f064077-b666-45ca-90c7-20345dca635c)

A base de dados foi construída com Postgres e alocada no serviço da RDS e o servidor da API foi hosteado pelo EC2 da AWS

A combinação dessas tecnologias agrupadas por container com Docker permitiu uma experiência de desenvolvimento ágil e escalável.

## Eu construí o APP de ponta a ponta e claro não podia faltar o Design no Figma! Segue o link para conferir a primeira versão da UI do app: <a href="https://www.figma.com/file/0Sbd9zPWhrXPuIBFwRwhaO/Whats-the-game%3F?type=design&node-id=0%3A1&mode=design&t=n8SRF9rSRn7VS0be-1" >Clique aqui!</a>

## Utilização

O aplicativo móvel foi desenvolvido para ser um jogo de entreterimento diário para o usuário, analisando a pontuação de cada usuário e ranqueando por pontos.
O projeto conta com um sistema de monetização por anúncios, e também um sistema de inscrição IN-APP para usuarios que queiram apoiar o projeto e em troca se abster de anúncios.


![mainMockupPronto](https://github.com/Mazzotti1/WhatsTheGame/assets/70278577/c865f571-e5e8-45ca-8521-81b1ec708064)


A API foi desenvolvida com segurança e encriptação de dados de ponta a ponta, com as mais diversas funções. Os usuários podem além de tentar descobrir qual é o jogo diário,
ele também pode passar o tempo com desafios tais como enigmas e anagramas para ser resolvido!

![minigamesMockupPronto](https://github.com/Mazzotti1/WhatsTheGame/assets/70278577/6c763646-3bef-4e7e-beff-f246ff60c44b)


No momento que o usuário entra no aplicativo ele recebe um mini tutorial para cada tela que entra, para se habituar as funções do aplicativo


Quando o usuário está logado ele também libera a opção de poder registrar suas pontuações, para competir com seus adversários no rank global!

![rankMokcupPronto](https://github.com/Mazzotti1/WhatsTheGame/assets/70278577/3eb4b35d-5be2-401f-9cf8-23fe84f667ba)


## O projeto é composto por:

- **API única:** Uma API desenvolvida especificamente para esse projeto, com intuito de ser extremamente funcional e segura;

- **Registro, Login e controle de perfil:** Usuário tem a possibilidade de se registrar, se logar e assinar um plano vip.

- **Banco de dados:** Banco de dados desenvolvido com PostgresSql, para agilizar ainda mais o processo da criação de tabelas e unificação da minha API, e deixar o banco mais escalavel com valor de custo mais baixo.

- **Firebase bucket:** Para controlar as fotos de capas dos jogos diários eu optei por em vez de salvar localmente no dispositivo a foto escolhida, ou salvar no meu próprio banco de dados , eu criei um bucket no Firebase da Google para armazenar em uma pasta específica com o nome do jogo que foi adcionado a foto, e no meu banco de dados eu salvo apenas o URI na tabela de usuário, então assim eu diminuí drasticamente o uso do meu espaço no banco de dados para controle de fotos.

- **AWS EC2:** Para hospedar minha API em um servidor para rodar 24/7, eu usei o EC2 da AWS, criei uma VM com ubuntu e conectei com a instanância que havia criado do EC2 por conexão ssh, então clonei minha API para dentro da VM e rodei com a biblioteca PM2

- **Google Auth:** O usuário tem a possibilidade de se registrar com uma conta google e sempre se logar com a mesma apenas clicando em "Continuar com Google", essa implementação ajuda muito aqueles usuários que preferem a praticidade ao se registrar e logar, e pelo fato do flutter também ser desenvolvido pelo Google, a utilização do Firebase é extremamente fácil .

![wtgMockupPronto](https://github.com/Mazzotti1/WhatsTheGame/assets/70278577/45955e0e-7e5c-487a-a37b-e3789d0ba705)


## Principais tecnologias utilizadas:

<div>
    <img src="https://img.shields.io/badge/KOTLIN-000B1D?style=for-the-badge&logo=KOTLIN&logoColor=white" />
    <img src="https://img.shields.io/badge/JAVA-000B1D?style=for-the-badge&logo=JAVA&logoColor=white" />
    <img src="https://img.shields.io/badge/POSTGRESQL-000B1D.svg?style=for-the-badge&logo=POSTGRESQL&logoColor=%white" /> 
    <img src="https://img.shields.io/badge/SPRINGBOOT-000B1D?style=for-the-badge&logo=SPRINGBOOT&logoColor=white" /> 
    <img src="https://img.shields.io/badge/DOCKER-000B1D.svg?style=for-the-badge&logo=DOCKER&logoColor=white" /> 
    <img src="https://img.shields.io/badge/FIREBASE-000B1D.svg?style=for-the-badge&logo=FIREBASE&logoColor=white" /> 
    <img src="https://img.shields.io/badge/AWS-000B1D.svg?style=for-the-badge&logo=amazon-aws&logoColor=white" />
    <img src="https://img.shields.io/badge/GOOGLE-000B1D.svg?style=for-the-badge&logo=google&logoColor=white" /> 
  
</div>

## Engenharia de usabilidade e acessibilidade do aplicativo <br>

Para garantir a melhor experiência de usuário possível, o aplicativo foi desenvolvido com base em conceitos de engenharia de usabilidade, tornando a interação com ele intuitiva e eficiente. Além disso, a acessibilidade foi um requisito fundamental durante o processo de desenvolvimento, com a inclusão de recursos que permitem que todas as pessoas possam utilizar o aplicativo, independentemente de suas habilidades físicas ou cognitivas.

## Grandes dificuldades encontradas:

  Criação e desenvolvimento de uma base de dados que fosse firme robusta e eficaz
   
   Responsividade para todos os dispositivos, eu criei desde o inicio o frontend com o máximo de valores "Flex" para que a tela fosse responsiva na maioria dos dispositivos móveis, mas acredito que eu poderia ter dado mais atenção a essa questão.
   
## Melhorias que podem ser implementadas:

- **1)**  Além das estatísticas e números que os usuários podem compartilhar e criar uma comunidade treino, seria muito interessante em um futuro adcionar sistema de ranks e conquistas, e prêmios visuais para treinos ou recordes batidos

---

<table>
    <td>
      Feito por <a href="https://github.com/Mazzotti1">Gabriel Mazzotti</a>
    </td>
</table>
<table>
    <td>
      
    </td>
</table>

