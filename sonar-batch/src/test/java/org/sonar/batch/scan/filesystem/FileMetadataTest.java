/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.batch.scan.filesystem;

import com.google.common.base.Charsets;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.fest.assertions.Assertions.assertThat;

public class FileMetadataTest {

  private static final String EXPECTED_HASH_WITHOUT_LATEST_EOL = "c80cc50d65ace6c4eb63f189d274dbeb";
  private static final String EXPECTED_HASH_NEW_LINE_FIRST = "cf2d41454b5b451eeb5122f0848c1d2a";
  private static final String EXPECTED_HASH_WITH_LATEST_EOL = "bf77e51d219e7d7d643faac86f1b5d15";
  private static final String NON_ASCII = "4050369e8ba432c9079e258b43fe4ab5";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Rule
  public TemporaryFolder temp = new TemporaryFolder();

  @Test
  public void empty_file() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.touch(tempFile);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(0);
    assertThat(metadata.hash).isNotEmpty();
    assertThat(metadata.originalLineOffsets).containsOnly(0);
    assertThat(metadata.lineChecksum).isEmpty();
  }

  @Test
  public void windows_without_latest_eol() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "foo\r\nbar\r\nbaz", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(3);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_WITHOUT_LATEST_EOL);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 5, 10);
    assertThat(metadata.lineChecksum).containsOnly(2090263731, 2090104836, 193487042);
  }

  @Test
  public void non_ascii_utf_8() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "föo\r\nbàr\r\n\u1D11Ebaßz\r\n", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(4);
    assertThat(metadata.hash).isEqualTo(NON_ASCII);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 5, 10, 18);
    assertThat(metadata.lineChecksum).containsOnly(2090410746, 2090243139, -931663839, 5381);
  }

  @Test
  public void non_ascii_utf_16() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "föo\r\nbàr\r\n\u1D11Ebaßz\r\n", Charsets.UTF_16, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_16);
    assertThat(metadata.lines).isEqualTo(4);
    assertThat(metadata.hash).isEqualTo(NON_ASCII);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 5, 10, 18);
    assertThat(metadata.lineChecksum).containsOnly(2090410746, 2090243139, -931663839, 5381);
  }

  @Test
  public void unix_without_latest_eol() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "foo\nbar\nbaz", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(3);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_WITHOUT_LATEST_EOL);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 4, 8);
    assertThat(metadata.lineChecksum).containsOnly(2090263731, 2090104836, 193487042);
  }

  @Test
  public void unix_with_latest_eol() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "foo\nbar\nbaz\n", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(4);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_WITH_LATEST_EOL);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 4, 8, 12);
    assertThat(metadata.lineChecksum).containsOnly(2090263731, 2090104836, 2090105100, 5381);
  }

  @Test
  public void mix_of_newlines_with_latest_eol() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "foo\nbar\r\nbaz\n", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(4);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_WITH_LATEST_EOL);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 4, 9, 13);
    assertThat(metadata.lineChecksum).containsOnly(2090263731, 2090104836, 2090105100, 5381);
  }

  @Test
  public void mix_of_newlines_without_latest_eol() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "foo\nbar\r\nbaz", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(3);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_WITHOUT_LATEST_EOL);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 4, 9);
    assertThat(metadata.lineChecksum).containsOnly(2090263731, 2090104836, 193487042);
  }

  @Test
  public void start_with_newline() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "\nfoo\nbar\r\nbaz", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(4);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_NEW_LINE_FIRST);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 1, 5, 10);
    assertThat(metadata.lineChecksum).containsOnly(177583, 2090263731, 2090104836, 193487042);
  }

  @Test
  public void start_with_bom() throws Exception {
    File tempFile = temp.newFile();
    FileUtils.write(tempFile, "\uFEFFfoo\nbar\r\nbaz", Charsets.UTF_8, true);

    FileMetadata.Metadata metadata = FileMetadata.INSTANCE.read(tempFile, Charsets.UTF_8);
    assertThat(metadata.lines).isEqualTo(3);
    assertThat(metadata.hash).isEqualTo(EXPECTED_HASH_WITHOUT_LATEST_EOL);
    assertThat(metadata.originalLineOffsets).containsOnly(0, 4, 9);
    assertThat(metadata.lineChecksum).containsOnly(2090263731, 2090104836, 193487042);
  }

  @Test
  public void should_throw_if_file_does_not_exist() throws Exception {
    File tempFolder = temp.newFolder();
    File file = new File(tempFolder, "doesNotExist.txt");

    thrown.expect(IllegalStateException.class);
    thrown.expectMessage("Fail to read file '" + file.getAbsolutePath() + "' with encoding 'UTF-8'");

    FileMetadata.INSTANCE.read(file, Charsets.UTF_8);
  }

  @Test
  public void line_feed_is_included_into_hash() throws Exception {
    File file1 = temp.newFile();
    FileUtils.write(file1, "foo\nbar\n", Charsets.UTF_8, true);

    // same as file1, except an additional return carriage
    File file1a = temp.newFile();
    FileUtils.write(file1a, "foo\r\nbar\n", Charsets.UTF_8, true);

    File file2 = temp.newFile();
    FileUtils.write(file2, "foo\nbar", Charsets.UTF_8, true);

    String hash1 = FileMetadata.INSTANCE.read(file1, Charsets.UTF_8).hash;
    String hash1a = FileMetadata.INSTANCE.read(file1a, Charsets.UTF_8).hash;
    String hash2 = FileMetadata.INSTANCE.read(file2, Charsets.UTF_8).hash;
    assertThat(hash1).isEqualTo(hash1a);
    assertThat(hash1).isNotEqualTo(hash2);
  }
}
