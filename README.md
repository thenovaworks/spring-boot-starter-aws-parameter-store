# spring-boot-starter-aws-parameter-store


AWS Systems Manager Parameter Store 는 애플리케이션, CLI 툴 등을 위한 구성 정보나 암호화된 문자열과 같은 비밀 데이터를 계층 구조의 중앙화된 방식으로 저정하고 관리합니다. 

spring-boot-starter-aws-parameter-store 프로젝트는 spring-boot 의 Auto Configuration 컴포넌트로 간단한 설정만으로 AWS Systems Manager Parameter Store 에 저장된 구성 정보를 쉽게 참조할 수 있습니다. 


AWS Systems Manager Parameter Store 의 장점은 다음과 같습니다. 

- 중앙 집중식 구성 관리: 구성 데이터와 비밀 정보를 중앙에서 관리함으로써, 모든 애플리케이션에서 일관된 설정 값을 사용할 수 있습니다.
- 보안성: 보안이 필요한 데이터는 KMS (Key Management Service)를 통해 암호화 하여 저장 합니다.
- 스케일링: 저장된 데이터는 AWS 클라우드 내에서 전역적으로 사용이 가능 합니다.
- 간단한 API 인터페이스: API 인터페이스를 통해 다양한 클라이언트에서 쉽게 데이터를 읽고 쓸 수 있습니다.
- 범용성: EC2 인스턴스, Lambda 함수, CodeBuild, CodeDeploy 등의 여러 애플리케이션의 런타임 보안 문자열 및 구성 정보를 쉽게 참조할 수 있습니다.

 

<br>

## Usage

이 모듈은 Spring Framework 제공 하는 확장 기능 중 하나인 [BeanPostProcessor](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-factory-extension-bpp) 을 이용 하여, 
`@SsmParameterValue` 어노테이션에 해당하는 속성 값을 Spring Bean 에 자동적으로 주입합니다. 

<br>

- `/dev/rds/apple/username` 경로의 값을 String 객체로 바인딩 하는 예제입니다. 

```
    @SsmParameterValue("/dev/rds/apple/username")
    private String username;
```

Bean 클래스의 username 속성은 `/dev/rds/apple/username` SSM Parameter Store 경로에 지정된 보안 문자열 값이 설정 됩니다. 


<br>


- `/dev/rds/apple` 경로에 포함된 모든 값들을 Map 객체로 바인딩 하는 예제입니다.
```
    @SsmParameterValue(value = "/dev/rds/apple", type = ValueType.MAP)
    private Map<String, String> info;
```
Bean 클래스의 info 속성은 `/dev/rds/apple` 경로에 포함된 모든 보안 문자열 값이 Map 객체로 설정 됩니다. (예: {database=apple, password=encrypted_secured_value, username=symplesims} ) 


<br>


## What to do First?

AWS SSM Parameter Store 를 액세스 하려면 Spring Boot 애플리케이션이 제대로 동작 하도록 spring-boot-starter-aws-parameter-store 를 추가 하기만 하면 됩니다.

- Maven

```
    <dependencies>
        <dependency>
          <groupId>io.github.thenovaworks</groupId>
          <artifactId>spring-boot-starter-aws-parameter-store</artifactId>
          <version>1.0.0</version>
        </dependency>
    </dependencies>
```

- Gradle

```
dependencies {
	implementation 'io.github.thenovaworks:spring-boot-starter-aws-parameter-store:0.9.5'
}
```

### Application Properties

spring-boot 의 [application-properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html) 설정 방식과 동일 하게 설정 합니다.

`application.yaml` 또는 `application.properties` 설정 파일에 아래와 같이 AWS Parameter Store 를 액세스 할 수 있도록 "spring.cloud.aws.ssm.provider-type" 속성을 설정 합니다.

#### For Production

Production 서비스 환경을 위해 "spring.cloud.aws.ssm.provider-type" 값을 "default" 으로 설정 합니다.

이렇게 하면, EC2, ECS, Lambda 와 같은 애플리케이션을 구현 했을 때 인증을 위해 내부적으로 [AssumeRole](https://docs.aws.amazon.com/STS/latest/APIReference/API_AssumeRole.html) 을 사용 하게 되고,
[DefaultCredentialsProvider](https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/auth/credentials/DefaultCredentialsProvider.html) 을 통해 자동적으로 인증 하게 됩니다.
이렇게 하면 소스 코드 에서 accessKey 가 노출 되지 않고 안전 하게 액세스 할 수 있습니다. 

```
spring:
  cloud:
    aws:
      ssm:
        provider-type: default
```

#### For Local Test

로컬 테스트 환경을 위해선 아래와 같이 "provider-type" 과 "profile" 속성을 설정 하고 AWS SSM Parameter Store 에 저장된 경로의 보안 값을 액세스 할 수 있는지 확인 할 수 있습니다.      
AWS Profile 에 관련된 설정은 AWS [Configuration and credential file settings](https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html) 가이드를 참고 합니다.  



- AWS Profile 을 참조하여 보안 문자열을 액세스 합니다.
```
spring:
  cloud:
    aws:
      ssm:
        provider-type: profile
        profile: <your_profile>
```


- AWS Environments 환경 변수를 참조하여 보안 문자열을 액세스 합니다.

```
spring:
  cloud:
    aws:
      ssm:
        provider-type: environment
```

### Spring Bean

아래 `HelloSpringBean` 을 참고하여 쉽게 사용할 수 있습니다.    
```

import io.github.thenovaworks.spring.aws.ssm.autoconfigure.SsmParameterValue;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HelloSpringBean {

    @SsmParameterValue("/dev/rds/apple/username")
    private String username;

    @SsmParameterValue(value = "/dev/rds/apple")
    private Map<String, String> info;

}

```


<br>

## Appendix

### spring-cloud-aws-starter-parameter-store

AWS SSM 파라미터 값을 참조하는 기능은 [spring-cloud](https://spring.io/projects/spring-cloud) 프로젝트의 [spring-cloud-aws-starter-parameter-store](https://github.com/awspring/spring-cloud-aws/tree/main/spring-cloud-aws-starters/spring-cloud-aws-starter-parameter-store) 모듈에서 이미 훌륭하고 안정적으로 구현해 놓았습니다.    
참고로, 해당 기능은 [PropertySource](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#features.external-config) 에 Parameter Value 값을 주입하고 @Value 어노테이션으로 액세스 합니다. 



### Reference Documentation


* [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/docs/3.0.x/reference/html/)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.0.x/maven-plugin/reference/htmlsingle/)


