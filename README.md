# bootcamp-backend
Repositório Backend


site para encriptar no login e criação do usuario.


site1 = https://stackblitz.com/edit/cryptojs-aes-encrypt-decrypt?file=index.js


validar token jwt
site2 = https://www.jwt.io/


para ações com usuario pegar um token neste endpoint campo "publicKey"
e o id ambos dentro do body

passo 1
postman request 'http://localhost:8081/bootcamp/security/cadastro' \
--header 'Content-Type: application/json' \
--body ''

response:

{
    "statusCode": 200,
    "message": "ok",
    "body": {
        "id": 854,
        "publicKey": "6e0355fd-2f40-4f33-bcce-277eb0164cb9",
        "screen": "cadastro",
        "userId": null
    }
}


passo 2
no header o token é o id obtido na chamada anterior
o password hash é sua senha + a public key  codificada no site 1

postman request POST 'http://localhost:8081/bootcamp/user/new' \
--header 'token: 402' \
--header 'Content-Type: application/json' \
--body '{
"name": "deivid",
"email": "deividvenancio@gmail.com",
"sobrenome": "ferreira venancio",
"passwordHash": "U2FsdGVkX19hw5KjkC1l+wgO8vbVgg4pvdWoUGvXwYI="
}'


LOGAR NO SISTEMA
pegar um novo token e id repetindo o passo 1

o token é o id do do passo anterior
login ficou assim

email + }*{ + senha
encriptar no site 1 com a publickey o codigo gerado vai no campo login

postman request POST 'http://localhost:8081/bootcamp/user/login' \
--header 'token: 853' \
--header 'Content-Type: application/json' \
--body '{
"login": "U2FsdGVkX1/VD/Wml5bHuNATjVxhsRmllq7uxoUKvA3EzertHYP6/ogeg9m2md3gTfeJQc/ce19EpkM+AyMe0g=="
}'

