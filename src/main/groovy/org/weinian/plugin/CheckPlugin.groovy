package org.weinian.plugin


import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.compile.JavaCompile


class CheckPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        CheckPluginProperty checkDependency = project.extensions.create('checkDependency', CheckPluginProperty)
        project.task("checkDependencies", {
            group = "build"
            description = "检查是否有快照依赖"
            doLast {
                checkDependencies(project)
            }
        })

        project.task("itjBootJar", {
            group = "build"
            description = "打包功能"
            dependsOn {
                project.tasks.findAll { task -> task.name.startsWith('bootJar') }
            }
            doLast {
                checkDependencies(project)
            }
        })

        project.task("checkBootJarAfter", {
            description = "构建配置"
            doLast {
                Boolean isDep = checkDependency.isCheckSnapshot == null || checkDependency.isCheckSnapshot
                if (isDep) {
                    checkDependencies(project)
                }

            }
        })
        //任务插入
        project.afterEvaluate {
            def bootJar = project.tasks.findByName("bootJar")
            bootJar.finalizedBy(project.tasks.findByName("checkBootJarAfter"))
        }

    }

    private void checkDependencies(Project project) {
        JavaCompile compile = project.getTasks().withType(JavaCompile.class).getByName("compileJava")
        def snapshots = compile.getClasspath()
                .filter { !(it.path ==~ /(?i)${project.rootProject.projectDir.toString().replace('\\', '\\\\')}.*build.libs.*/) }
                .filter { it.path =~ /(?i)SNAPSHOT/ }
                .collect { it.name }
                .unique()
        if (!snapshots.isEmpty()) {
            throw new GradleException("Please get rid of snapshots for following dependencies before releasing $snapshots")
        }
    }
}