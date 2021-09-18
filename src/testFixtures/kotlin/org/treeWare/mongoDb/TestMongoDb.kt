package org.treeWare.mongoDb

import de.flapdoodle.embed.mongo.Command
import de.flapdoodle.embed.mongo.MongodExecutable
import de.flapdoodle.embed.mongo.MongodStarter
import de.flapdoodle.embed.mongo.Paths
import de.flapdoodle.embed.mongo.config.Defaults
import de.flapdoodle.embed.mongo.config.MongoCmdOptions
import de.flapdoodle.embed.mongo.config.MongodConfig
import de.flapdoodle.embed.mongo.config.Net
import de.flapdoodle.embed.mongo.distribution.Feature
import de.flapdoodle.embed.mongo.distribution.Versions
import de.flapdoodle.embed.process.distribution.Distribution
import de.flapdoodle.embed.process.distribution.Platform
import de.flapdoodle.embed.process.runtime.Network

object TestMongoDb {
    private val port = Network.getFreeServerPort()

    private const val mongoDbVersion = "4.2.16"
    private val command = Command.MongoD
    private val runtimeConfig = Defaults.runtimeConfigFor(command).artifactStore(
        Defaults.extractedArtifactStoreFor(command).withDownloadConfig(
            Defaults.downloadConfigFor(command).packageResolver(MongoDbPackageResolver(command)).build()
        )
    ).build()
    private val mongodConfig = MongodConfig.builder()
        .version(
            Versions.withFeatures(
                de.flapdoodle.embed.process.distribution.Version.of(mongoDbVersion),
                Feature.SYNC_DELAY,
                Feature.NO_HTTP_INTERFACE_ARG
            )
        )
        .net(Net(port, Network.localhostIsIPv6()))
        .cmdOptions(
            MongoCmdOptions.builder().from(MongoCmdOptions.defaults())
                .useNoPrealloc(false)
                .useSmallFiles(false)
                .build()
        )
        .build()

    private val mongodStarter = MongodStarter.getInstance(runtimeConfig)
    private var mongodExecutable: MongodExecutable? = null

    val uri = "mongodb://localhost:$port"

    fun start() {
        mongodExecutable = mongodStarter.prepare(mongodConfig)
        mongodExecutable?.start()
    }

    fun stop() {
        mongodExecutable?.stop()
    }
}

class MongoDbPackageResolver(command: Command) : Paths(command) {
    override fun getPath(distribution: Distribution): String {
        val version = getVersionPart(distribution.version())
        return when (distribution.platform()) {
            Platform.OS_X -> "osx/mongodb-macos-x86_64-$version.tgz"
            else -> super.getPath(distribution)
        }
    }
}
