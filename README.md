# Spring Rest Full API - HATEOAS
HATEOAS é um nível extra após o REST e é usado para apresentar informações sobre a API REST ao cliente, permitindo uma melhor compreensão da API sem a necessidade de mostrar a especificação ou a documentação. 

Isso é feito através da inclusão de links em uma resposta retornada e usando apenas esses links para se comunicar mais com o servidor. Isso reduz o provável capô do cliente quebrando devido a mudanças no serviço. Se houver alguns pontos finais estáticos que o cliente possa fazer uso e outras chamadas sejam feitas através dos links incluídos na resposta, o código do cliente não deve quebrar (não estou afirmando que é 100% seguro). Isso faz com que a suposição de que os links retornados com a resposta já implementou os verbos REST padrão.

## Começando

Como sempre, começamos a olhar para as dependências necessárias (as dependências do Lombok + MySQL foram usadas, mas não são mostradas abaixo). spring-boot-starter-hateoas contém a spring-boot-starter-web dependência para que você não precise incluir isso como você provavelmente faria ao criar uma API REST com Spring Boot.

```xml
<dependency>		  
	<groupId>org.springframework.boot</groupId>		 
	<artifactId>spring-boot-starter-hateoas</artifactId>		
</dependency>
```

Também vale a pena notar que a spring-boot-starter-parent versão 2.0.0.M5 foi usada nesta exemplo.

```xml 
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>2.0.0.M5</version>
</parent>
```

## Visualizando o controlador Rest
Agora que temos as dependências fora do caminho, acho que o primeiro código que devemos observar é uma versão reduzida de um controlador de restrição que implementou o HATEOAS.

```java
@RestController		
@RequestMapping(value = "/people", produces = "application/hal+json")		
public class PersonController {		

  private IPersonRepository personRepository;	

  public PersonController(PersonRepository personRepository) {
  	this.personRepository = personRepository;		  
  }		

  @GetMapping		  
  public ResponseEntity<Resources<PersonResource>> all() {		    
  	// GET all		  
  }

  @GetMapping("/{id}")		  
  public ResponseEntity<PersonResource> get(@PathVariable final long id) {		    // GET		  
  }	

  @PostMapping		  
  public ResponseEntity<PersonResource> post(@RequestBody final Person personFromRequest) {		    
  	// POST		  
  }		

  @PutMapping("/{id}")		  
  public ResponseEntity<PersonResource> put(@PathVariable("id") final long id, @RequestBody Person personFromRequest) {		   
  	// PUT		  
  }

  @DeleteMapping("/{id}")		  
  public ResponseEntity<?> delete(@PathVariable("id") final long id) {		   
  	// DELETE
  }		

}
```

A razão pela qual cortei muito o código neste exemplo é para que possamos olhar para as partes individuais sem muito barulho. Então, o que temos acima? Um serviço REST básica com os verbos de descanso GET, POST, PUTe DELETEimplementada juntamente com uma recuperar todos método. Cada ponto de extremidade retorna um ResponseEntitycom a maioria deles (não DELETE) contendo a PersonResource/ Resources<PersonResource>. É aí que o serviço HATEOAS diferencia do serviço REST padrão que normalmente retornaria ResponseEntityo objeto que contém o objeto (ou o objeto diretamente) em vez deste objeto de recurso. Nesse cenário ele retorna ResponseEntity<PersonResource>(HATEOAS) em vez de ResponseEntity<Person>(REST).

Então, qual é este objeto de recurso que eu continuo mencionando? É simplesmente um invólucro que contém o objeto que você normalmente retornaria mais URIs (links) para os pontos finais relacionados que podem ser usados. Uma maneira de fazer isso parecer um pouco mais clara é que o PersonResourcemostrado no exemplo acima também pode ser escrito como Resource<Person>onde Personé o objeto que você normalmente retornaria. Abaixo, JSON seria retornado de uma GETsolicitação para um serviço REST padrão seguido pela saída de um serviço HATEOAS para a mesma chamada.

```
CURL localhost:8080/people/1
```

### REST Service

```json
{		  
	"id": 1,		  
	"firstName": "test",		  
	"secondName": "one",		  
	"dateOfBirth": "01/01/0001 01:10",		  
	"profession": "im a test",		  
	"salary": 0		
}
```

### HATEOAS Service

```json
{		  
	"person": {		    
		"id": 1,		    
		"firstName": "test",		    
		"secondName": "one",		    
		"dateOfBirth": "01/01/0001 01:10",		    
		"profession": "im a test",		    
		"salary": 0		  
	},
	"_links": {		    
		"people": {		      
			"href": "http://localhost:8090/people"		    
		},		    
		"memberships": {		      
			"href": "http://localhost:8090/people/1/memberships"		    
		},		    
		"self": {		      
			"href": "http://localhost:8090/people/1"		    
		}		  
	}
}
```

Como você pode ver, há muito mais acontecendo na resposta HATEOAS devido a todos os links que foram incluídos. Os URIs neste exemplo podem não ser os mais úteis, mas espero demonstrar a idéia com bastante satisfação. 

A partir do pedido original que foi feito, você tem links que mostram onde recuperar todas as pessoas e todas as associações para essa pessoa e, finalmente, um link "auto" que apontar para o pedido que acabou de ser feito. Agora que temos uma idéia melhor do que é o objeto de recurso, podemos dar uma olhada em como PersonResourceé implementado.

```java 
@Getter		
public class PersonResource extends ResourceSupport {	

  private Person person;	

  public PersonResource(Person person) {		    
  	this.person = person;		    
    long id = person.getId();		    
    add(linkTo(PersonController.class).withRel("people"));		    
    add(linkTo(methodOn(GymMembershipController.class).all(id)).withRel("memberships"));		    
    add(linkTo(methodOn(PersonController.class).get(id)).withSelfRel());		  
   }		
}
```

Não há muito para esta classe, pois é apenas um construtor com o resto dos métodos e funcionalidades que ele precisa ser fornecido ResourceSupport. Há duas coisas que acontecem aqui, o objeto que deve ser retornado é armazenado com um getter sendo criado (feito por Lombok aqui) e os links para recursos relacionados são criados. Permite quebrar uma das linhas de código que adiciona um link ao recurso para ver o que está acontecendo.

```java 
add(linkTo(methodOn(GymMembershipController.class).all(id)).withRel("memberships"));
```

add é um método herdado do ResourceSupport qual adiciona o link passado para ele. linkTo cria o link e methodOnobtém o URI para o GymMembershipController.allmétodo ( people/{id}/memberships), ambos métodos são métodos estáticos ControlLinkBuilder. O id passado foi passado para o all método, permitindo que {id} o URI seja substituído pelo valor de entrada. Uma vez que o link é criado withRel é chamado para fornecer um nome para descrever como ele está relacionado ao recurso. As outras linhas abordam a criação de links de maneiras ligeiramente diferentes, um manualmente cria um novo Link objeto e outro usa withSelfRel que simplesmente denomina a relação como "auto".

Agora, temos uma melhor compreensão do que é um recurso, podemos observar o código real dentro dos métodos do controlador, espero que tenha escolhido os códigos de resposta corretos, caso contrário, estou certo de que alguém tentará corrigir-me ... Vou então explicar dois deles em maior profundidade, enquanto o mesmo conceito é executado por todos eles.

```java
@RestController		
@RequestMapping(value = "/people", produces = "application/hal+json")		
public class PersonController {		

  private IPersonRepository personRepository;	

  public PersonController(PersonRepository personRepository) {		    
  	this.personRepository = personRepository;		  
  }	

  @GetMapping		  
  public ResponseEntity<Resources<PersonResource>> all() {		    
  	List<PersonResource> collection = personRepository.findAll().stream().map(PersonResource::new).collect(Collectors.toList());		    
  	Resources<PersonResource> resources = new Resources<>(collection);		    
	String uriString = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();		    resources.add(new Link(uriString, "self"));		    
	return ResponseEntity.ok(resources);		  
  }		

  @GetMapping("/{id}")		  
  public ResponseEntity<PersonResource> get(@PathVariable final long id) {		    
  	return personRepository.findById(id).map(p -> ResponseEntity.ok(new PersonResource(p)))		        .orElseThrow(() -> new PersonNotFoundException(id));		  
  }	

  @PostMapping		  
  public ResponseEntity<PersonResource> post(@RequestBody final Person personFromRequest) {		    
  	final Person person = new Person(personFromRequest);		    
  	personRepository.save(person);		    
  	final URI uri =	MvcUriComponentsBuilder.fromController(getClass()).path("/{id}")		            .buildAndExpand(person.getId()).toUri();		    
  	return ResponseEntity.created(uri).body(new PersonResource(person));		  
  }

  @PutMapping("/{id}")		  
  public ResponseEntity<PersonResource> put(@PathVariable("id") final long id, @RequestBody Person personFromRequest) {		    
  	final Person person = new Person(personFromRequest, id);		    
  	personRepository.save(person);		    
  	final PersonResource resource = new PersonResource(person);		    
  	final URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();		    
  	return ResponseEntity.created(uri).body(resource);		  
  }		

  @DeleteMapping("/{id}")		  
  public ResponseEntity<?> delete(@PathVariable("id") final long id) {		    
  	return personRepository.findById(id).map( p -> {		              
  		personRepository.deleteById(id);		              
  			return ResponseEntity.noContent().build();		            
  		})		        
  		.orElseThrow(() -> new PersonNotFoundException(id));		  
  }		
}
```

Então, nós o temos, HATEOAS ( H epermedia A s T e E ngine O f A pplication S tate) é construído com base em uma API REST para desacoplar o cliente do servidor, diminuindo o número de pontos de extremidade codificados que o cliente pode acessar . 

Em vez disso, eles são chamados através de links dentro de recursos que são retornados pelos pontos finais estáticos que o servidor fornece. Isso também diminui a chance de que o cliente seja interrompido quando o serviço muda, pois depende dos nomes dos links em vez de seus URIs. 

Como sempre o Spring Boot vem equipado com tudo o que precisamos para começar a funcionar com velocidade razoável, assumindo o spring-boot-starter-hateoas está incluído, claro! Em uma nota de encerramento, é necessário ou não HATEOAS incluir uma API REST ainda parece estar discutindo devido à complexidade que ela adiciona ao projetar o serviço e porque exige que o cliente seja escrito de forma diferente quando comparado a um que faz solicitações para um serviço REST padrão. Dito isto, se você decide que vale a pena usar ou não, pelo menos você agora tem uma compreensão sobre como criar um com Spring Boot!

