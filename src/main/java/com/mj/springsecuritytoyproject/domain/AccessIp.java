package com.mj.springsecuritytoyproject.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "ACCESS_IP")
@Data
@EqualsAndHashCode(of = "id")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessIp {

    @Id
    @GeneratedValue
    @Column(name = "IP_ID")
    private Long id;

    @Column(name = "IP_ADDRESS", nullable = false)
    private String ipAddress;

}
