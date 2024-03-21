# VM Creator

## Téléchargement

Télécharger et dézipper l'application.

## Microsoft Entra

Il va falloir modifier les différentes valeurs présentes dans le fichier application.properties.
L'emplacement du fichier est
``
src/main/resources/application.properties
``

Les valeurs à modifier sont celles de la configuration Azure :
```
AZURE_TENANT_ID={your tenant id}
AZURE_CLIENT_ID={your client id}
AZURE_CLIENT_SECRET={your client secret}
AZURE_SUBSCRIPTION_ID={your subscription id}
```

### Retrouver les différentes valeurs

Créer une nouvelle application sur [Microsoft Entra](https://entra.microsoft.com/#view/Microsoft_AAD_RegisteredApps/ApplicationsListBlade/quickStartType~/null/sourceType/Microsoft_AAD_IAM) : 

![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/2534921d-cc3e-4744-94fc-98080f829401)

![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/27d2f2a8-e6a1-4961-89be-92935c6b8a48)


Sur le screen ci-dessous, on peut reporter plusieurs valeurs :

```
AZURE_TENANT_ID = "b7b023b8-7c32-4c02-92a6-c8cdaa1d189c"
AZURE_CLIENT_ID = "0f24ec3d-48f3-4fcc-a4b6-f61d0372f1c2"
```

AZURE_TENANT_ID correspond à Directory (tenant) ID sur le screen, tandis que AZURE_CLIENT_ID correspond à Application (client) ID.

![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/f1b0b4a6-0a01-44b0-8fe6-8f0350942fda)

Pour la valeur AZURE_CLIENT_SECRET, il faut cliquer sur Add a certificate or Secret, puis New client secret :

![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/d67f15db-b4b3-4d3a-a616-9544a131c238)
On peut reporter la valeur qui est située dans la variable Value.
![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/474183df-338c-47dd-8cd6-f4b0edba11d4)

```
AZURE_CLIENT_SECRET = 
```

Pour finir, l'ID d'abonnement est disponible sur votre page Azure située [ici](https://portal.azure.com/#@supdevinci.fr/resource/subscriptions/db6cd703-8c6f-484c-a74d-a0256606fca3/overview).

### Attribuer un rôle à l’application

Il faut maintenant donner le rôle à l'application.
Se connecter d'abord au portail Azure, puis cliquer sur son abonnement Azure for Students puis Contrôle d’accès (IAM).
Sélectionnez Ajouter, puis Ajouter une attribution de rôle. Choisir Rôles d’administrateur privilégié puis Contributeur.

![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/fa17e62a-7359-43ac-a24a-3d992bdee34b)

Sous l’onglet Membres, sélectionnez Attribuer l’accès à, puis sélectionnez Utilisateur, groupe ou principal du service, sélectionnez Sélectionner des membres.
Il faut ensuite taper le nom de l'appli puis pour finir sélectionner Vérifier + attribuer.

![image](https://github.com/Ranjinie-Souria/VMCreator/assets/36516479/24343596-6482-453c-b5bc-891e0a513253)
