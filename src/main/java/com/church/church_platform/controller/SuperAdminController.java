//package com.church.church_platform.controller;
//
//public class SuperAdminController {
//    // SuperAdminController.java
//    @PutMapping("/api/super-admin/churches/{id}/verify")
//    public ResponseEntity<?> verifyChurch(
//            @PathVariable Long id) {
//        Church church = churchRepository.findById(id)
//                .orElseThrow(() ->
//                        new RuntimeException("Church not found!")
//                );
//        church.setVerified(true);
//        churchRepository.save(church);
//        return ResponseEntity.ok(
//                Map.of("message", "Church verified! ✅")
//        );
//    }
//}
